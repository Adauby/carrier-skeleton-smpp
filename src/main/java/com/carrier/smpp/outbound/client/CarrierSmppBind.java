package com.carrier.smpp.outbound.client;

import static com.carrier.util.Messages.UNBINDING;
import static com.carrier.util.Values.DEFAULT_ENQUIRE_LINK_INTERVAL;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.carrier.smpp.smsc.request.SmscPduRequestHandler;
import com.carrier.smpp.smsc.response.SmscPduResponseHandler;
import com.carrier.util.ThreadUtil;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.cloudhopper.smpp.util.SmppSessionUtil;

public class CarrierSmppBind implements Runnable{
	private Logger logger = LogManager.getLogger(CarrierSmppBind.class);
	private long timeToSleep = 100;
	private Long id;
	private PduQueue pduQueue;
	private SmppSessionConfiguration config;
	private boolean unbound = false;
	private SmppSession session = null;
	private RequestSender requestSender;
	private Npi npi;
	private Ton ton;
	private int tps;
	private RequestSender enquireLinkSender;
	private int enquireLinkInterval = DEFAULT_ENQUIRE_LINK_INTERVAL;
	private final Map<Integer, SmscPduRequestHandler> smscReqHandlers;
	private final Map<Integer, SmscPduResponseHandler> smscResponseHandlers;
	public CarrierSmppBind(PduQueue pduQueue, SmppSessionConfiguration config, RequestSender requestSender
			,RequestSender enquireLinkSender,Map<Integer, SmscPduRequestHandler> smscReqHandlers
			,Map<Integer, SmscPduResponseHandler> smscResponseHandlers,int tps) {

		this.pduQueue = pduQueue;
		this.config = config;
		this.requestSender =requestSender;
		this.enquireLinkSender = enquireLinkSender;
		this.tps = tps;
		this.smscReqHandlers = smscReqHandlers;
		this.smscResponseHandlers = smscResponseHandlers;
	}

	public Long getId() {
		return id;
	}
	

	@Override
	public void run() {
		
		while(!unbound) {
			try {
				connect();
				requestSender.send(session, pduQueue, pduQueue.size());
				enquireLinkSender.send(session,enquireLinkInterval);
				timeToSleep = 100;
			} catch (SmppTimeoutException e) {
				logger.info(e);

			}catch(InterruptedException e) {
				logger.warn(e);
				SmppSessionUtil.close(session);
				Thread currentThread = Thread.currentThread();
				currentThread.interrupt();

			} catch (SmppChannelException |  UnrecoverablePduException  e) {
				logger.warn(e);
				SmppSessionUtil.close(session);
				/*
				 * Wait 30 seconds before trying again...
				 */
				timeToSleep = 30000;
			}finally {
				if(!unbound)
					ThreadUtil.sleep(timeToSleep);
			}
		}
		logger.info(UNBINDING);
		SmppSessionUtil.close(session);

	}

	private void connect() throws SmppTimeoutException,
	SmppChannelException, UnrecoverablePduException, InterruptedException {
		DefaultSmppSessionHandler sessionHandler=null;
		if (!unbound &&(this.session == null || this.session.isClosed())) {
			SharedClientBootstrap sharedClientBootstrap = SharedClientBootstrap.getInstance();
			DefaultSmppClient clientBootstrap = sharedClientBootstrap.getClientBootstrap();
			sessionHandler= new ClientSmppSessionHandler(config.getName(),logger,pduQueue,smscReqHandlers,smscResponseHandlers);
			this.session = clientBootstrap.bind(config, sessionHandler);
		}
	}
	
	public CarrierSmppBind self() {
		return this;
	}

	public boolean isUp() {
		if(session==null) return false;
		return session.isBound();
	}

	public boolean isUnbound() {
		return unbound;
	}

	public void setUnbound(boolean unbound) {
		this.unbound = unbound;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Npi getNpi() {
		return npi;
	}

	public void setNpi(Npi npi) {
		this.npi = npi;
	}

	public Ton getTon() {
		return ton;
	}

	public void setTon(Ton ton) {
		this.ton = ton;
	}

	public int getTps() {
		return tps;
	}

	public void setTps(int tps) {
		this.tps = tps;
	}

	public void unbind() {
		if(session!=null && session.isBound())
			session.unbind(10000);
	}
	

}