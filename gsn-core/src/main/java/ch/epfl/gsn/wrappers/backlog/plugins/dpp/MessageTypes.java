package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

public class MessageTypes {

	/* general message types */
	public static final int DPP_MSG_TYPE_INVALID      		= 0;    /* unknown / invalid message type */
	public static final int DPP_MSG_TYPE_TIMESYNC     		= 1;    /* timestamp */
	public static final int DPP_MSG_TYPE_EVENT        		= 2;    /* event notification */
	public static final int DPP_MSG_TYPE_NODE_INFO    		= 3;    /* node info (sent after a reset) */
	public static final int DPP_MSG_TYPE_CMD          		= 4;    /* command message */
	public static final int DPP_MSG_TYPE_FW           		= 5;    /* FW update message */
	public static final int DPP_MSG_TYPE_HEALTH_MIN         = 6;    /* minimal health message (APP + COM combined) */
	
	/* message types concerning the communication processor */
	public static final int DPP_MSG_TYPE_COM_RESPONSE		= 10;   /* command response */
	public static final int DPP_MSG_TYPE_COM_HEALTH   		= 11;   /* com processor periodic health message */
	public static final int DPP_MSG_TYPE_LWB_HEALTH   		= 12;   /* periodic LWB health message */
	
	/* message types concerning the application processor */
	public static final int DPP_MSG_TYPE_APP_HEALTH   		= 21;   /* app processor periodic health message */
	
	/* application-specific message types (must start from 30U) */
	public static final int DPP_MSG_TYPE_AE_EVENT     		= 32;   /* acoustic emission event */
	public static final int DPP_MSG_TYPE_AE_DATA      		= 33;   /* acoustic emission data */
	public static final int DPP_MSG_TYPE_GNSS_SV      		= 34;   /* GNSS SV message */
	public static final int DPP_MSG_TYPE_WGPS_STATUS  		= 35;   /* Wireless GPS  */
	public static final int DPP_MSG_TYPE_GEOPHONE_ACQ 		= 36;   /* Geophone acquire data */
	public static final int DPP_MSG_TYPE_IMU          		= 37;   /* IMU data */
	public static final int DPP_MSG_TYPE_GEOPHONE_ADCDATA   = 38;   /* Geophone event acquistion (ADC) data */
	public static final int DPP_MSG_TYPE_INCLINO       		= 39;   /* inclinometer data */

	/* event-driven specific message types */
	public static final int DPP_MSG_TYPE_STAG_WAKEUP		= 41;   /* Staggered Wakeup message */
	public static final int DPP_MSG_TYPE_ACK_COMMAND		= 42;   /* Acknowledgment from the Base Station combined with a command to the sensor nodes */
	public static final int DPP_MSG_TYPE_HEALTH_AGGR		= 43;   /* Aggregated minimal health message */
	public static final int DPP_MSG_TYPE_GEO_ACQ_AGGR		= 44;   /* Aggregated minimal geophone acquisition event */

	  /* no types below this */
	public static final int DPP_MSG_TYPE_LASTID       		= 127;
}

