package com.carrier.smpp.outbound.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.carrier.smpp.model.esme.EsmeAccountRepository;
import com.carrier.smpp.model.esme.EsmeSmppAccount;

public class CarrierSmppConnectorTest {
	private final static Logger logger = LogManager.getLogger(CarrierSmppConnectorTest.class);
	public static final int PORT = 2785;
	public static final String SYSTEMID = "smppclient1";
	public static final String PASSWORD = "password";
	private static final String MESSAGE_ID = "fcc45-523kl-j8ep";

	private class EsmeAccountRepTest implements EsmeAccountRepository{
		final EsmeSmppAccount account;
		private EsmeAccountRepTest(EsmeSmppAccount account) {
			this.account = account;
		}
		@Override
		public EsmeSmppAccount findBySystemId(String systemId) {
			return account;
		}
	}
	/*@Before
	public void resetSessionManager() throws NoSuchFieldException, SecurityException, IllegalArgumentException
	, IllegalAccessException {
		Field instance = SessionManager.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(null, null);
	}


	@Test
	public void testConnectionToSmsc() throws InterruptedException, HandlerException {
		Map<Integer, EsmeRequestHandler>requestHandlers = new HashMap<>();
		Map<Integer, EsmeResponseHandler> responseHandlers = new HashMap<>();
		List<ConfigParameter>parameterCheckers = Arrays.asList(new DefaultSystemIdParameter(),new DefaultPasswordParameter());
		CarrierSmppServerHandler carrierSmppServerHandler = new CarrierSmppServerHandler(
				new DefaultEsmeBindRequestHandler(parameterCheckers,new EsmeAccountRepTest(new EsmeAccount(SYSTEMID, PASSWORD)))
				, new EsmeAccountRepTest(new EsmeAccount(SYSTEMID, PASSWORD)),requestHandlers,responseHandlers);

		CarrierSmppServer smppServer = new CarrierSmppServer(getExecutor(),getMonitorExecutor()
				, new SmppSrvConfigLoader(), carrierSmppServerHandler);
		smppServer.start();
		ConnectorConfiguration connectorConfig = new ConnectorConfiguration("test.carrier.0",SYSTEMID, PASSWORD, "localhost", PORT);
		connectorConfig.setHost("127.0.0.1");
		
		Map<Integer, SmscPduResponseHandler>respHandlers = new HashMap<>();
		Map<Integer, SmscPduRequestHandler>reqHandlers = new HashMap<>();
		reqHandlers.put(SmppConstants.CMD_ID_DELIVER_SM, new deliverSmHandler());
		respHandlers.put(SmppConstants.CMD_ID_SUBMIT_SM_RESP, new SubmitSmRespHandler());
		PduRequestSender pduRequestSender = new PduRequestSender();
		MaxTpsDefault maxTps = new MaxTpsDefault();

		BindTypes bindTypes = new BindTypes();
		CarrierSmppConnector connector = new CarrierSmppConnector(connectorConfig,BindExecutor::runBind
        		,pduRequestSender,new DefaultEnquireLinkSender(),maxTps,reqHandlers,respHandlers);

		connector.connect();
		
		assertEquals(1, connector.getBinds().size());

		for(CarrierSmppBind bind: connector.getBinds())
			assertEquals(true,bind.isUp());

		connector.disconnect();
		SharedClientBootstrap sharedClientBootstrap = SharedClientBootstrap.getInstance();
		BindExecutor.stopAll();
		sharedClientBootstrap.stopClientBootStrap();
		smppServer.stop();


	}

	private class SmppSrvConfigLoader implements ConfigurationLoader<SmppServerConfiguration>{
		@Override
		public SmppServerConfiguration loadConfig() {
			SmppServerConfiguration configuration = new SmppServerConfiguration();
			configuration.setPort(PORT);
			configuration.setSystemId("carrier-skeleton");
			return configuration;
		}

	}

	private class SubmitSmHandler implements EsmeRequestHandler {

		@Override
		public PduResponse handleRequest(PduRequest pduRequest, EsmeSmppSession esmeSmppSession) {
			SubmitSm submitSm = (SubmitSm)pduRequest;
			SubmitSmResp submitSmResp = submitSm.createResponse();
			submitSmResp.setMessageId(MESSAGE_ID);
			return submitSmResp;
		}

	}

	private class UnbindHandler implements EsmeRequestHandler {

		@Override
		public PduResponse handleRequest(PduRequest pduRequest, EsmeSmppSession esmeSmppSession) {
			Unbind unbind = (Unbind)pduRequest;
			return unbind.createResponse();
		}

	}
	


}

class PduRequestSender implements RequestSender{
	private final Logger logger = LogManager.getLogger(PduRequestSender.class);
	@Override
	public void send(SmppSession session, PduQueue pduQueue, int tps) throws InterruptedException {
		if(session.isBound() && !pduQueue.isEmpty()) {
			try {
				
			SmppSessionConfiguration config=  session.getConfiguration();
			sendMessage(session,tps,canSubmit(config.getType()),pduQueue);
			}catch(UnrecoverablePduException  | SmppChannelException e) {
				SmppSessionUtil.close(session);
			}
		}
		
	}
	
	public boolean sendMessage(SmppSession session, int maxToSend
			,boolean canSend,PduQueue pduQueue) throws UnrecoverablePduException, SmppChannelException,InterruptedException {
		PduRequest asynchronSubmit=null;
		boolean submited=false;
		try {
			if(canSend) {
				for(int i=0 ;i<maxToSend;i++) {
					asynchronSubmit = pduQueue.takeFirstRequest();
					session.sendRequestPdu(asynchronSubmit,session.getConfiguration().getRequestExpiryTimeout(), false);				
					logger.info(asynchronSubmit);	
				}
				submited=true;
			}
		}catch( RecoverablePduException | SmppTimeoutException e) {
			logger.error(e);
		}finally {
			if(!submited && asynchronSubmit!=null)
				pduQueue.addRequestFirst(asynchronSubmit);
		}

		return submited;
	}
	
	public boolean canSubmit(SmppBindType type) {
		return type.equals(SmppBindType.TRANSCEIVER)||type.equals(SmppBindType.TRANSMITTER);
	}

	@Override
	public void send(SmppSession session, int requestInterval) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
}
class SubmitSmRespHandler implements SmscPduResponseHandler{
	private final Logger logger = LogManager.getLogger(SubmitSmRespHandler.class);
	@Override
	public void handleResponse(PduAsyncResponse pduAsyncResponse) {
		SubmitSmResp resp = (SubmitSmResp)pduAsyncResponse.getResponse();
		logger.info("handling submitSm resp: "+resp);
	}
	
}

class deliverSmHandler implements SmscPduRequestHandler {
	private final Logger logger = LogManager.getLogger(deliverSmHandler.class);
	@Override
	public PduResponse handle(PduRequest pduRequest) {
		DeliverSm deliver = (DeliverSm)pduRequest;
		logger.info("handling deliverSm: " + deliver);
		return deliver.createResponse();
	}*/
	
}



