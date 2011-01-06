/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.slee.enabler.ims.userprofile;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.Tracer;

import org.mobicents.slee.resource.diameter.sh.events.avp.userdata.ObjectFactory;

import net.java.slee.resource.diameter.sh.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientProvider;
import net.java.slee.resource.diameter.sh.events.DiameterShMessage;
import net.java.slee.resource.diameter.sh.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.events.UserDataRequest;
import net.java.slee.resource.diameter.sh.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.events.avp.IdentitySetType;
import net.java.slee.resource.diameter.sh.events.avp.RequestedDomainType;
import net.java.slee.resource.diameter.sh.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.events.avp.userdata.ShData;
import net.java.slee.resource.diameter.sh.events.avp.userdata.UserDataObjectFactory;

/**
 * IMS User Profile SLEE Enabler.
 * 
 * @author <a href=mailto:brainslog@gmail.com> Alexandre Mendonca </a>
 */
public abstract class IMSUserProfileChildSbb implements Sbb, IMSUserProfileChild {

  private static Tracer tracer;

  protected SbbContext sbbContext;

  protected ShClientProvider diameterShClientSbbInterface = null;
  protected ShClientActivityContextInterfaceFactory diameterShClientACIF = null;

  protected ShClientMessageFactory diameterShClientMessageFactory = null;
  protected DiameterShAvpFactory diameterShClientAvpFactory = null;

  public abstract RequestMappingACI asSbbActivityContextInterface(ActivityContextInterface aci);

  // -- SBB LOCAL OBJECT METHODS ----------------------------------------------

  public UserDataObjectFactory getUserDataObjectFactory() {
    return new ObjectFactory();
  }
  
  public String getRepositoryData(String publicIdentity, byte[][] serviceIndications) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, null);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.REPOSITORY_DATA);
    udr.setServiceIndications(serviceIndications);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getIMSPublicIdentity(String publicIdentity, byte[] msisdn, int identitySet) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, msisdn);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.IMS_PUBLIC_IDENTITY);
    udr.setIdentitySet(IdentitySetType.fromInt(identitySet));

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getIMSUserState(String publicIdentity) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, null);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.IMS_USER_STATE);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getSCSCFName(String publicIdentity) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, null);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.S_CSCFNAME);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getInitialFilterCriteria(String publicIdentity, String serverName) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, null);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.INITIAL_FILTER_CRITERIA);
    udr.setServerName(serverName);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getLocationInformation(byte[] msisdn, int requestedDomain) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(null, msisdn);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.LOCATION_INFORMATION);
    udr.setRequestedDomain(RequestedDomainType.fromInt(requestedDomain));

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getUserState(byte[] msisdn, int requestedDomain) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(null, msisdn);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.USER_STATE);
    udr.setRequestedDomain(RequestedDomainType.fromInt(requestedDomain));

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getChargingInformation(String publicIdentity, byte[] msisdn) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, msisdn);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.CHARGING_INFORMATION);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getMSISDN(String publicIdentity, byte[] msisdn) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, msisdn);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.MSISDN);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  public String getPSIActivation(String publicIdentity) throws IOException {
    UserIdentityAvp publicIdentityAvp = createUserIdentityAvp(publicIdentity, null);

    UserDataRequest udr = diameterShClientMessageFactory.createUserDataRequest(publicIdentityAvp, DataReferenceType.PSI_ACTIVATION);

    ShClientActivity activity = getShClientActivity();
    activity.sendUserDataRequest(udr);

    return activity.getSessionId();
  }

  // -- HELPER METHODS --------------------------------------------------------

  private UserIdentityAvp createUserIdentityAvp(String publicIdentity, byte[] msisdn) {
    UserIdentityAvp userIdentityAvp = diameterShClientAvpFactory.createUserIdentity();

    if (publicIdentity != null) {
      userIdentityAvp.setPublicIdentity(publicIdentity);
    }
    else if (msisdn != null && msisdn.length > 0) {
      userIdentityAvp.setMsisdn(new String(msisdn));
    }

    return userIdentityAvp;
  }

  private String[] getUserIdentityValues(DiameterShMessage shMessage) {
    UserIdentityAvp uIdAvp = null;

    if(shMessage instanceof UserDataRequest) {
      uIdAvp = ((UserDataRequest)shMessage).getUserIdentity();
    }
    else if(shMessage instanceof ProfileUpdateRequest) {
      uIdAvp = ((ProfileUpdateRequest)shMessage).getUserIdentity();
    }
    else if(shMessage instanceof SubscribeNotificationsRequest) {
      uIdAvp = ((SubscribeNotificationsRequest)shMessage).getUserIdentity();
    }
    else if(shMessage instanceof PushNotificationRequest) {
      uIdAvp = ((PushNotificationRequest)shMessage).getUserIdentity();
    }

    String publicIdentity = null;
    String msisdn = null;
    if(uIdAvp != null) {
      publicIdentity = uIdAvp.getPublicIdentity();
      msisdn = uIdAvp.getMsisdn();
      if(publicIdentity == null && msisdn == null) {
        tracer.warning("Unable to retrieve Public User/Service Identity OR MSISDN. At least one of them should be present.");
      }
    }
    else {
      tracer.warning("User-Identity AVP missing in UDR.");
    }

    return new String[]{publicIdentity, msisdn};
  }

  private ShClientActivity getShClientActivity() throws IOException {
    try {
      ShClientActivity activity = diameterShClientSbbInterface.createShClientActivity();
      ActivityContextInterface aci = diameterShClientACIF.getActivityContextInterface(activity);
      aci.attach(sbbContext.getSbbLocalObject());
      return activity;
    }
    catch (Exception e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  // -- EVENT HANDLERS FOR DIAMETER REQUESTS ----------------------------------

  public void onSubscribeNotificationsAnswer(SubscribeNotificationsAnswer event, RequestMappingACI aci) {
    SubscribeNotificationsRequest snr = (SubscribeNotificationsRequest) aci.getRequest();

    aci.detach(sbbContext.getSbbLocalObject());

    String [] userIdentityValues = getUserIdentityValues(snr);
    SubsReqType subsReqType = snr.getSubsReqType();
    long resultCode = event.getResultCode();

    // only one data ref should be present.. but at least one must!
    DataReferenceType dataRef = snr.getDataReferences()[0];
    switch(dataRef.getValue()) {
    case DataReferenceType._REPOSITORY_DATA:
      // hack.. to be fixed in Resource Adaptor
      String[] serviceIndicationStrings = snr.getServiceIndications();
      byte[][] serviceIndicationBytes = new byte[serviceIndicationStrings.length][];
      for (int i = 0; i < serviceIndicationStrings.length; i++) {
        serviceIndicationBytes[i] = serviceIndicationStrings[i].getBytes();
      }

      getParentSbbCMP().subscribeRepositoryDataResponse(userIdentityValues[0], serviceIndicationBytes, subsReqType.getValue(), resultCode);
      break;
    case DataReferenceType._IMS_USER_STATE:
      getParentSbbCMP().subscribeIMSUserStateResponse(userIdentityValues[0], subsReqType.getValue(), resultCode);
      break;
    case DataReferenceType._S_CSCFNAME:
      getParentSbbCMP().subscribeSCSCFNameResponse(userIdentityValues[0], subsReqType.getValue(), resultCode);
      break;
    case DataReferenceType._INITIAL_FILTER_CRITERIA:
      getParentSbbCMP().subscribeInitialFilterCriteriaResponse(userIdentityValues[0], snr.getServerName(), subsReqType.getValue(), resultCode);
      break;
    case DataReferenceType._PSI_ACTIVATION:
      getParentSbbCMP().subscribePSIActivationResponse(userIdentityValues[0], subsReqType.getValue(), resultCode);
      break;
    default:
      //
    }
  }

  public void onProfileUpdateAnswer(ProfileUpdateAnswer event, RequestMappingACI aci) {
    ProfileUpdateRequest pur = (ProfileUpdateRequest) aci.getRequest();

    aci.detach(sbbContext.getSbbLocalObject());
    DataReferenceType dataRef = pur.getDataReference();

    String [] userIdentityValues = getUserIdentityValues(pur);
    long resultCode = event.getResultCode();

    switch(dataRef.getValue()) {
    case DataReferenceType._REPOSITORY_DATA:
      getParentSbbCMP().updateRepositoryDataResponse(userIdentityValues[0], resultCode);
      break;
    case DataReferenceType._PSI_ACTIVATION:
      getParentSbbCMP().updatePSIActivationResponse(userIdentityValues[0], resultCode);
      break;
      default:
        //
    }

  }

  public void onPushNotificationRequest(PushNotificationRequest event, RequestMappingACI aci) {
    // TODO: Fill method
  }

  public void onUserDataAnswer(UserDataAnswer event, RequestMappingACI aci) {
    UserDataRequest udr = (UserDataRequest) aci.getRequest();

    aci.detach(sbbContext.getSbbLocalObject());

    ShData data = null;

    try {
      data = event.getUserDataObject();
    }
    catch (Exception e) {
      tracer.severe("Unable to extract User-Data from User-Data-Answer.", e);
      return;
    }

    String [] userIdentityValues = getUserIdentityValues(udr);
    long resultCode = event.getResultCode();

    // only one data ref should be present.. but at least one must!
    DataReferenceType dataRef = udr.getDataReferences()[0];
    switch(dataRef.getValue()) {
    case DataReferenceType._REPOSITORY_DATA:
      getParentSbbCMP().deliverRepositoryData(userIdentityValues[0], udr.getServiceIndications(), resultCode, data);
      break;
    case DataReferenceType._IMS_PUBLIC_IDENTITY:
      IdentitySetType identitySet = udr.getIdentitySet();
      getParentSbbCMP().deliverIMSPublicIdentity(userIdentityValues[0], userIdentityValues[1].getBytes(), identitySet != null ? identitySet.getValue() : null, resultCode, data);
      break;
    case DataReferenceType._IMS_USER_STATE:
      getParentSbbCMP().deliverIMSUserState(userIdentityValues[0], resultCode, data);
      break;
    case DataReferenceType._S_CSCFNAME:
      getParentSbbCMP().deliverSCSCFName(userIdentityValues[0], resultCode, data);
      break;
    case DataReferenceType._INITIAL_FILTER_CRITERIA:
      getParentSbbCMP().deliverInitialFilterCriteria(userIdentityValues[0], udr.getServerName(), resultCode, data);
      break;
    case DataReferenceType._LOCATION_INFORMATION:
      RequestedDomainType requestedDomain = udr.getRequestedDomain();
      getParentSbbCMP().deliverLocationInformation(userIdentityValues[1].getBytes(), requestedDomain != null ? requestedDomain.getValue() : null, resultCode, data);
      break;
    case DataReferenceType._USER_STATE:
      requestedDomain = udr.getRequestedDomain();
      getParentSbbCMP().deliverUserState(userIdentityValues[1].getBytes(), requestedDomain != null ? requestedDomain.getValue() : null, resultCode, data);
      break;
    case DataReferenceType._CHARGING_INFORMATION:
      getParentSbbCMP().deliverChargingInformation(userIdentityValues[0], userIdentityValues[1].getBytes(), resultCode, data);
      break;
    case DataReferenceType._MSISDN:
      getParentSbbCMP().deliverMSISDN(userIdentityValues[0], userIdentityValues[1].getBytes(), resultCode, data);
      break;
    case DataReferenceType._PSI_ACTIVATION:
      getParentSbbCMP().deliverPSIActivation(userIdentityValues[0], resultCode, data);
      break;
    default:
      //
    }
  }

  // -- CMP FIELDs ------------------------------------------------------------

  /**
   * Setter for parentSbbCMP CMP field.
   * 
   * @param parentSbb
   */
  public abstract void setParentSbbCMP(IMSUserProfileParentSbbLocalObject parentSbb);

  /**
   * Getter for parentSbbCMP CMP field.
   * 
   * @return
   */
  public abstract IMSUserProfileParentSbbLocalObject getParentSbbCMP();

  // -- SBB OBJECT LIFECYCLE METHODS ------------------------------------------

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbActivate()
   */
  @Override
  public void sbbActivate() {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbCreate()
   */
  @Override
  public void sbbCreate() throws CreateException {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbExceptionThrown(java.lang.Exception, java.lang.Object, javax.slee.ActivityContextInterface)
   */
  @Override
  public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbLoad()
   */
  @Override
  public void sbbLoad() {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbPassivate()
   */
  @Override
  public void sbbPassivate() {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbPostCreate()
   */
  @Override
  public void sbbPostCreate() throws CreateException {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbRemove()
   */
  @Override
  public void sbbRemove() {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbRolledBack(javax.slee.RolledBackContext)
   */
  @Override
  public void sbbRolledBack(RolledBackContext arg0) {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#sbbStore()
   */
  @Override
  public void sbbStore() {
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#setSbbContext(javax.slee.SbbContext)
   */
  @Override
  public void setSbbContext(SbbContext sbbContext) {
    this.sbbContext = sbbContext;
    if (tracer == null) {
      tracer = sbbContext.getTracer(IMSUserProfileChildSbb.class.getSimpleName());
    }
    try {
      Context context = (Context) new InitialContext().lookup("java:comp/env");
      diameterShClientSbbInterface = (ShClientProvider) context.lookup("slee/resources/diameter-sh-client-ra-interface");
      diameterShClientACIF = (ShClientActivityContextInterfaceFactory) context.lookup("slee/resources/JDiameterShClientResourceAdaptor/java.net/0.8.1/acif");

      diameterShClientMessageFactory = diameterShClientSbbInterface.getClientMessageFactory();
      diameterShClientAvpFactory = diameterShClientSbbInterface.getClientAvpFactory();
    }
    catch (NamingException e) {
      tracer.severe("Can't set sbb context.", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.Sbb#unsetSbbContext()
   */
  @Override
  public void unsetSbbContext() {
    this.sbbContext = null;
  }
}
