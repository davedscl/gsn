package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.wrappers.backlog.plugins.DPPMessagePlugin;

public interface Message {
	
	public boolean initialize(DPPMessagePlugin dppMessagePlugin, String coreStationName, String deploymentName);

	int getType();
	
	boolean isMinimal();

	Serializable[] receivePayload(ByteBuffer payload) throws Exception;

	ByteBuffer sendPayload(String action, String[] paramNames, Object[] paramValues) throws Exception;
	
	Serializable[] sendPayloadSuccess(boolean success);

	DataField[] getOutputFormat();
}
