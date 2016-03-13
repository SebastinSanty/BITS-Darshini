/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bits.protocolanalyzer.analyzer.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bits.protocolanalyzer.analyzer.GenericAnalyzer;
import com.bits.protocolanalyzer.analyzer.PacketWrapper;
import com.bits.protocolanalyzer.persistence.entity.NetworkAnalyzerEntity;
import com.bits.protocolanalyzer.persistence.repository.NetworkAnalyzerRepository;
import com.google.common.eventbus.EventBus;

/**
 *
 * @author amit
 * @author crygnus
 */

@Component
public class NetworkAnalyzer implements GenericAnalyzer {

    @Autowired
    private NetworkAnalyzerRepository networkAnalyzerRepository;

    private EventBus networkLayerEventBus;

    public void setEventBus(EventBus eventBus) {
        this.networkLayerEventBus = eventBus;
    }

    public void publishToEventBus(PacketWrapper packetWrapper) {
        /* post the event to corresponding event-bus */
        this.networkLayerEventBus.post(packetWrapper);
    }

    public void analyzePacket(PacketWrapper packetWrapper) {

        // analyze and pass to hooks
        NetworkAnalyzerEntity nae = new NetworkAnalyzerEntity();
        nae.setPacketIdEntity(packetWrapper.getPacketIdEntity());
        networkAnalyzerRepository.save(nae);

        publishToEventBus(packetWrapper);

    }
}
