package javax.slee.usage;

import javax.slee.management.NotificationSource;
import javax.slee.management.ManagementException;

/**
 * The <code>UsageNotificationManagerMBean</code> interface defines the basic common
 * functionality required for the management of usage notifications.
 * <p>
 * <p>
 * The base JMX Object Name of a <code>UsageNotificationManagerMBean</code> object is
 * specified by the {@link #BASE_OBJECT_NAME} constant.  The {@link #NOTIFICATION_SOURCE_KEY}
 * constant specifies the Object Name property that identifies the type of the
 * notification source for the Usage Notification Manager MBean.  In addition to this
 * property, each notification source includes additional properties in the Usage
 * Notification Manager MBean Object Name with the property keys indicated below:
 * <ul>
 *   <li>For notification sources of type {@link javax.slee.management.SbbNotification}:
 *       <ul>
 *         <li>{@link javax.slee.management.SbbNotification#SERVICE_NAME_KEY}
 *         <li>{@link javax.slee.management.SbbNotification#SERVICE_VENDOR_KEY}
 *         <li>{@link javax.slee.management.SbbNotification#SERVICE_VERSION_KEY}
 *         <li>{@link javax.slee.management.SbbNotification#SBB_NAME_KEY}
 *         <li>{@link javax.slee.management.SbbNotification#SBB_VENDOR_KEY}
 *         <li>{@link javax.slee.management.SbbNotification#SBB_VERSION_KEY}
 *       </ul>
 *   <li>For notification sources of type {@link javax.slee.management.ResourceAdaptorEntityNotification}:
 *       <ul>
 *         <li>{@link javax.slee.management.ResourceAdaptorEntityNotification#RESOURCE_ADAPTOR_ENTITY_NAME_KEY}
 *       </ul>
 *   <li>For notification sources of type {@link javax.slee.management.SubsystemNotification}:
 *       <ul>
 *         <li>{@link javax.slee.management.SubsystemNotification#SUBSYSTEM_NAME_KEY}
 *       </ul>
 * </ul>
 * <p>
 * A management client may obtain the complete Object Name of a Usage Notification Manager
 * MBean for an SBB via the {@link javax.slee.management.ServiceUsageMBean} interface.
 * The complete Object Name of a Usage Notification Manager MBean for a resource adaptor
 * entity may be obtained using the {@link javax.slee.management.ResourceManagementMBean}
 * interface.  The complete Object Name of a Usage Notification Manager MBean for a SLEE
 * internal component or subsystem may be obtained using the
 * {@link javax.slee.management.SleeManagementMBean} interface.
 * <p>
 * <b>Interface extension</b><br>
 * During deployment of a SLEE component that defines a usage parameters interface, the
 * <code>UsageNotificationManagerMBean</code> interface is extended to provide access to
 * managed attributes that allow the generation of usage notifications for each usage
 * parameter to be enabled or disabled.  For each usage parameter defined in the usage
 * parameters interface, a read-write managed attribute is added to this interface with
 * the following method signatures:
 * <p>
 * <ul><code>
 *     public boolean get<i>&lt;usage-parameter-name&gt;</i>NotificationsEnabled() throws ManagementException;<br>
 *     public void set<i>&lt;usage-parameter-name&gt;</i>NotificationsEnabled(boolean enabled) throws ManagementException;
 * </code></ul>
 * <p>
 * where
 * <ul><code><i>usage-parameter-name</i></code> is the name of the usage parameter, with
 * the first letter capitalized.</ul>
 * <p>
 * Usage notifications are enabled or disabled on a per-usage-parameter basis for each
 * notification source, ie. the SLEE does not take into consideration a specific usage parameter
 * set when deciding whether or not usage notifications are enabled for a particular usage
 * parameter. That means that if usage notifications are enabled for a particular usage
 * parameter, if that usage parameter is updated in <i>any</i> usage parameter set belonging
 * to the notification source, a usage notification will be generated by the SLEE.  Ideally,
 * usage notification generation should only be enabled for usage parameters that require
 * a management client (or notification filter) to be kept constantly up-to-date with the
 * status of the usage parameter.  For other cases, the current value of a usage parameter
 * can always be obtained by a management client by interacting with the {@link UsageMBean}
 * for the usage parameter set.
 * <p>
 * <b>Initial State of <code>NotificationsEnabled</code> Flags</b><br>
 * In order to maintain backwards compatibility with existing management clients, a
 * <code>UsageNotificationManagerMBean</code> that is generated by the SLEE for an SBB that
 * is installed using a SLEE 1.0 deployment descriptor (as determined by the deployment
 * descriptor's <code>DOCTYPE</code> declaration) defaults to having usage notifications
 * enabled for all usage parameters.
 * <p>
 * A <code>UsageNotificationManagerMBean</code> that is generated by the SLEE for an SBB
 * or resource adaptor that is installed using a SLEE 1.1 deployment descriptor defaults to
 * having usage notifications <i>disabled</i> for all usage parameters, unless an explicit
 * declaration is made in the deployment descriptor to enable notifications for specific
 * usage parameters.
 * @since SLEE 1.1
 */
public interface UsageNotificationManagerMBean {
    /**
     * The base JMX Object Name string of all SLEE Usage Notification Manager MBeans.
     * This string is equal to "javax.slee.usage:type=UsageNotificationManager" and
     * the string <code>BASE_OBJECT_NAME + ",*"</code> defines a JMX Object Name property
     * pattern which matches with all Usage Notification Manager MBeans that are registered
     * with the MBean Server.  A Usage Notification Manager MBean is registered with
     * the MBean Server using this base name in conjunction with the property specified
     * by {@link #NOTIFICATION_SOURCE_KEY} and additional properties depending on the
     * type of the notification source.
     */
    public static final String BASE_OBJECT_NAME = "javax.slee.usage:type=UsageNotificationManager";

    /**
     * The JMX Object Name property key that identifies the type of the notification
     * source that the Usage Notification Manager MBean is managing usage notification
     * generation for.  This key is equal to the string "notificationSource".  The value
     * of this key is equal to the <code>USAGE_NOTIFICATION_TYPE</code> constant defined
     * by the notification source.  For example, if this Usage Notification Manager MBean
     * was managing usage notifications for an SBB, the Object Name of the Usage Notification
     * Manager MBean would contain a property with a key specified by this constant and a
     * value equal to {@link javax.slee.management.SbbNotification#USAGE_NOTIFICATION_TYPE}.
     * @see #BASE_OBJECT_NAME
     */
    public static final String NOTIFICATION_SOURCE_KEY = "notificationSource";

    
    /**
     * Get the notification source that this Usage Notification Manager MBean is managing
     * usage notification enabler flags for.
     * @return the notification source.
     * @throws ManagementException if the notification source could not be obtained due
     *        to a system-level failure.
     */
    public NotificationSource getNotificationSource()
        throws ManagementException;

    /**
     * Notify the SLEE that the Usage Notification Manager MBean is no longer required by
     * the management client.  As the SLEE may subsequently deregister the Usage Notification
     * Manager MBean from the MBean server, a client that invokes this method should assume
     * that the Object Name they had for the MBean is no longer valid once this method returns.
     * @throws ManagementException if the Usage Notification Manager MBean could not be closed
     *        by the SLEE due to a system-level failure.
     */
    public void close()
        throws ManagementException;
}