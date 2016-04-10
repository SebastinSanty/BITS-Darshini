package in.ac.bits.protocolanalyzer.analyzer.network;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import in.ac.bits.protocolanalyzer.analyzer.CustomAnalyzer;
import in.ac.bits.protocolanalyzer.analyzer.PacketWrapper;
import in.ac.bits.protocolanalyzer.analyzer.event.PacketTypeDetectionEvent;
import in.ac.bits.protocolanalyzer.persistence.entity.IPv4Entity;
import in.ac.bits.protocolanalyzer.persistence.repository.AnalysisRepository;
import in.ac.bits.protocolanalyzer.protocol.Protocol;
import in.ac.bits.protocolanalyzer.utils.BitOperator;
import in.ac.bits.protocolanalyzer.utils.ByteOperator;
import java.lang.String;
import java.util.Arrays;
import org.apache.commons.codec.binary.Hex;
import org.pcap4j.packet.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IPv4Analyzer implements CustomAnalyzer {
  private static final String PACKET_TYPE_OF_RELEVANCE = Protocol.IPV4;

  private byte[] ipv4Header;

  @Autowired
  private AnalysisRepository repository;

  private int startByte;

  private int endByte;

  private EventBus eventBus;

  public void configure(EventBus eventBus) {
    this.eventBus = eventBus;
    this.eventBus.register(this);
  }

  private void setIPv4Header(PacketWrapper packetWrapper) {
    Packet packet = packetWrapper.getPacket();
    int startByte = packetWrapper.getStartByte();
    byte[] rawPacket = packet.getRawData();
    this.ipv4Header = Arrays.copyOfRange(rawPacket, startByte, startByte + IPv4Header.TOTAL_HEADER_LENGTH);
  }

  public void setStartByte(PacketWrapper packetWrapper) {
    this.startByte = packetWrapper.getStartByte() + IPv4Header.TOTAL_HEADER_LENGTH;
  }

  public void setEndByte(PacketWrapper packetWrapper) {
    this.endByte = packetWrapper.getEndByte();
  }

  public void publishTypeDetectionEvent(String nextPacketType, int startByte, int endByte) {
    this.eventBus.post(new PacketTypeDetectionEvent(nextPacketType, startByte, endByte));
  }

  public byte getVersion(byte[] ipv4Header) {
    byte[] version = BitOperator.parse(ipv4Header, IPv4Header.VERSION_START_BIT, IPv4Header.VERSION_END_BIT);
    byte returnVar = ByteOperator.parseBytesbyte(version);
    return returnVar;
  }

  public byte getIhl(byte[] ipv4Header) {
    byte[] ihl = BitOperator.parse(ipv4Header, IPv4Header.IHL_START_BIT, IPv4Header.IHL_END_BIT);
    byte returnVar = ByteOperator.parseBytesbyte(ihl);
    return returnVar;
  }

  public short getDiffserv(byte[] ipv4Header) {
    byte[] diffserv = BitOperator.parse(ipv4Header, IPv4Header.DIFFSERV_START_BIT, IPv4Header.DIFFSERV_END_BIT);
    short returnVar = ByteOperator.parseBytesshort(diffserv);
    return returnVar;
  }

  public int getTotalLen(byte[] ipv4Header) {
    byte[] totallen = BitOperator.parse(ipv4Header, IPv4Header.TOTALLEN_START_BIT, IPv4Header.TOTALLEN_END_BIT);
    int returnVar = ByteOperator.parseBytesint(totallen);
    return returnVar;
  }

  public int getIdentification(byte[] ipv4Header) {
    byte[] identification = BitOperator.parse(ipv4Header, IPv4Header.IDENTIFICATION_START_BIT, IPv4Header.IDENTIFICATION_END_BIT);
    int returnVar = ByteOperator.parseBytesint(identification);
    return returnVar;
  }

  public byte getFlags(byte[] ipv4Header) {
    byte[] flags = BitOperator.parse(ipv4Header, IPv4Header.FLAGS_START_BIT, IPv4Header.FLAGS_END_BIT);
    byte returnVar = ByteOperator.parseBytesbyte(flags);
    return returnVar;
  }

  public short getFragOffset(byte[] ipv4Header) {
    byte[] fragoffset = BitOperator.parse(ipv4Header, IPv4Header.FRAGOFFSET_START_BIT, IPv4Header.FRAGOFFSET_END_BIT);
    short returnVar = ByteOperator.parseBytesshort(fragoffset);
    return returnVar;
  }

  public short getTtl(byte[] ipv4Header) {
    byte[] ttl = BitOperator.parse(ipv4Header, IPv4Header.TTL_START_BIT, IPv4Header.TTL_END_BIT);
    short returnVar = ByteOperator.parseBytesshort(ttl);
    return returnVar;
  }

  public String getProtocol(byte[] ipv4Header) {
    byte[] protocol = BitOperator.parse(ipv4Header, IPv4Header.PROTOCOL_START_BIT, IPv4Header.PROTOCOL_END_BIT);
    return Hex.encodeHexString(protocol);
  }

  public int getHdrChecksum(byte[] ipv4Header) {
    byte[] hdrchecksum = BitOperator.parse(ipv4Header, IPv4Header.HDRCHECKSUM_START_BIT, IPv4Header.HDRCHECKSUM_END_BIT);
    int returnVar = ByteOperator.parseBytesint(hdrchecksum);
    return returnVar;
  }

  public long getSrcAddr(byte[] ipv4Header) {
    byte[] srcaddr = BitOperator.parse(ipv4Header, IPv4Header.SRCADDR_START_BIT, IPv4Header.SRCADDR_END_BIT);
    long returnVar = ByteOperator.parseByteslong(srcaddr);
    return returnVar;
  }

  public long getDstAddr(byte[] ipv4Header) {
    byte[] dstaddr = BitOperator.parse(ipv4Header, IPv4Header.DSTADDR_START_BIT, IPv4Header.DSTADDR_END_BIT);
    long returnVar = ByteOperator.parseByteslong(dstaddr);
    return returnVar;
  }

  @Subscribe
  public void analyze(PacketWrapper packetWrapper) {
    if (PACKET_TYPE_OF_RELEVANCE.equalsIgnoreCase(packetWrapper.getPacketType())) {
      setIPv4Header(packetWrapper);
      String nextPacketType = setNextProtocolType();
      setStartByte(packetWrapper);
      setEndByte(packetWrapper);
      publishTypeDetectionEvent(nextPacketType, startByte, endByte);
      IPv4Entity entity = new IPv4Entity();
      entity.setPacketId(packetWrapper.getPacketId());
      entity.setTotalLen(getTotalLen(ipv4Header));
      entity.setDstAddr(getDstAddr(ipv4Header));
      entity.setDiffserv(getDiffserv(ipv4Header));
      entity.setVersion(getVersion(ipv4Header));
      entity.setHdrChecksum(getHdrChecksum(ipv4Header));
      entity.setIdentification(getIdentification(ipv4Header));
      entity.setSrcAddr(getSrcAddr(ipv4Header));
      entity.setFlags(getFlags(ipv4Header));
      entity.setFragOffset(getFragOffset(ipv4Header));
      entity.setIhl(getIhl(ipv4Header));
      entity.setProtocol(getProtocol(ipv4Header));
      entity.setTtl(getTtl(ipv4Header));
      IndexQuery query = new IndexQuery();
      query.setObject(entity);
      repository.save(query);
    }
  }

  public String setNextProtocolType() {
    String nextHeaderType = getProtocol(this.ipv4Header);
    switch(nextHeaderType) {
      case "06": return Protocol.TCP;
      default: return Protocol.END_PROTOCOL;
    }
  }
}
