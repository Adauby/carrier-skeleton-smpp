package com.carrier.smpp.outbound.client;

import com.cloudhopper.smpp.SmppSession;

public interface RequestSender {

	void send(SmppSession session,PduQueue pduQueue, int tps)throws InterruptedException;
	void send(SmppSession session,int requestInterval) throws InterruptedException;

}