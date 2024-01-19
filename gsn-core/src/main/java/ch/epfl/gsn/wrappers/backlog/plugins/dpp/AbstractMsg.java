package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.epfl.gsn.wrappers.backlog.plugins.DPPMessagePlugin;

public abstract class AbstractMsg implements Message {

	@Override
	public boolean initialize(DPPMessagePlugin dppMessagePlugin, String coreStationName, String deploymentName) {
		return true;
	}

	@Override
	public ByteBuffer sendPayload(String action, String[] paramNames, Object[] paramValues) throws Exception {
		throw new Exception("sendPayload not implemented");
	}

	@Override
	public boolean isMinimal() {
		return false;
	}

	@Override
	public Serializable[] sendPayloadSuccess(boolean success) {
		return null;
	}
	
	protected static Short convertUINT8(ByteBuffer buf) throws Exception {
		Short val = (short) (buf.get() & 0xFF);
		if (val == 0xFF){
			return null;
		} else {
			return val;
		}
			
	}
	
	protected static Short convertINT8(ByteBuffer buf) throws Exception {
		Short val = (short) buf.get();
		if (val == -128){
			return null;
		} else {
			return val;
		}
	}
	
	protected static Integer convertUINT16(ByteBuffer buf) throws Exception {
		Integer val = buf.getShort() & 0xFFFF;
		if (val == 0xFFFF){
			return null;
		} else {
			return val;
		}
	}
	
	protected static Integer convertINT16(ByteBuffer buf) throws Exception {
		Integer val = (int) buf.getShort();
		if (val == -32768){
			return null;
		} else {
			return val;
		}
	}
	
	protected static Long convertUINT32(ByteBuffer buf) throws Exception {
		Long val = buf.getInt() & 0xFFFFFFFFL;
		if (val == 0xFFFFFFFFL){
			return null;
		} else {
			return val;
		}
	}
	
	protected static Long convertINT32(ByteBuffer buf) throws Exception {
		Long val = (long) buf.getInt();
		if (val == -2147483648L){
			return null;
		} else {
			return val;
		}
	}
	
	protected static Long convertUINT64(ByteBuffer buf) throws Exception {
		return buf.getLong();
	}
}
