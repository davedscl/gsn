package ch.epfl.gsn.wrappers.backlog.plugins.dpp;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.epfl.gsn.beans.DataField;

public class EventMsg extends AbstractMsg {
	
	private static DataField[] dataField = {
			new DataField("COMPONENT_ID", "SMALLINT"),		/* component id */
			new DataField("TYPE", "SMALLINT"),				/* event type / id */
			new DataField("VALUE", "BIGINT")				/* event value / subtype */
			};

	@Override
	public Serializable[] receivePayload(ByteBuffer payload) throws Exception {
		Short component_id = null;
		Short type = null;
		Long value = null;
		
		try {
			type = convertUINT8(payload);
			component_id = convertUINT8(payload);
			value = convertUINT32(payload);
		} catch (Exception e) {
		}
        
		return new Serializable[]{component_id, type, value};
	}

	@Override
	public DataField[] getOutputFormat() {
		return dataField;
	}

	@Override
	public int getType() {
		return ch.epfl.gsn.wrappers.backlog.plugins.dpp.MessageTypes.DPP_MSG_TYPE_EVENT;
	}
}
