package com.carrier.smpp.server;

import static com.cloudhopper.smpp.SmppConstants.STATUS_OK;

import java.util.List;

import com.carrier.smpp.model.esme.EsmeAccountRepository;
import com.carrier.smpp.model.esme.EsmeSmppAccount;
import com.cloudhopper.smpp.SmppSessionConfiguration;

public class DefaultEsmeBindRequestHandler implements BindRequestHandler {
	private final List<SmppAccountParamCheckable>parameters;
	private final EsmeAccountRepository esmeAccountRepository;
	public DefaultEsmeBindRequestHandler(List<SmppAccountParamCheckable> parameters, EsmeAccountRepository esmeAccountRepository) {
		super();
		this.parameters = parameters;
		this.esmeAccountRepository = esmeAccountRepository;
	}

	@Override
	public int handleRequest(SmppSessionConfiguration sessionConfiguration) {
		EsmeSmppAccount configParams = esmeAccountRepository.find(sessionConfiguration);
		for(SmppAccountParamCheckable param : parameters) {
			int status = param.check(sessionConfiguration,configParams); 
			if(status !=STATUS_OK)
				return status;
		}
		return STATUS_OK;
	}

}
