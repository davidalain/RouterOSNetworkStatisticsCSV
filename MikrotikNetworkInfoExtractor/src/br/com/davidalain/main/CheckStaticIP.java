package br.com.davidalain.main;



import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.davidalain.common.Tuple;
import examples.Example;
import me.legrange.mikrotik.MikrotikApiException;

public class CheckStaticIP extends Example {

	public static void main(String... args) throws Exception {
		
		System.out.println("====* Check static IP use *====");
		
		CheckStaticIP ex = new CheckStaticIP();
		ex.connect();
		ex.test();
		ex.disconnect();
	}

	private void test() throws MikrotikApiException {
		
		List<String> ipDhcpLeaseList = new LinkedList<String>();
		List<Tuple<String, String>> dhcpLeaseTuplesList = new LinkedList<Tuple<String, String>>();
		List<String> ipArpList = new LinkedList<String>();
		List<String> simpleQueueList = new LinkedList<String>();

		List<Map<String, String>> dhcpLeaseMaps =  con.execute("/ip/dhcp-server/lease/print");
		List<Map<String, String>> arpMaps =  con.execute("/ip/arp/print");
		List<Map<String, String>> simpleQueues =  con.execute("/queue/simple/print");

		System.out.println("========================== Extraindo Simple Queues ==================");
		for (Map<String, String> result : simpleQueues) {
			simpleQueueList.add(result.get("name")); //Queue Name
		}

		System.out.println("============================ Simple Queue List ==================");
		Collections.sort(simpleQueueList);
		for(String str : simpleQueueList){
			System.out.println(str);
		}
		
		System.out.println("============================ Extraindo DHCP Leases ==================");
		for (Map<String, String> dhcpLease : dhcpLeaseMaps) {
			
			String ip = dhcpLease.get("address");
			String mac = dhcpLease.get("active-mac-address");
			Tuple<String, String> t = new Tuple<String, String>(ip, mac);
			
			ipDhcpLeaseList.add(ip);
			dhcpLeaseTuplesList.add(t);
		}

		System.out.println("============================ DHCP Leases List ==================");
		Collections.sort(ipDhcpLeaseList);
		for(String str : ipDhcpLeaseList){
			System.out.println(str);
		}

		System.out.println("===================== Extraindo ARP table ==================");
		for (Map<String, String> arp : arpMaps) {
			ipArpList.add(arp.get("address")); //IP address
		}

		System.out.println("====================== ARP List ====================");
		Collections.sort(ipArpList);
		for(String str : ipArpList){
			System.out.println(str);
		}
		
		System.out.println("===================== Removendo os IPs repetidos ===================");
		for(String ip : ipDhcpLeaseList){
			ipArpList.remove(ip); //Remove apenas se existir
		}
		
		System.out.println("=================== Mostrar os IP que estão fixos ============================");
		if(ipArpList.size() == 0){
			System.out.println("Nenhum dispositivo está utilizando IP fixo!!");
		}else{
			for(String ip : ipArpList){
				System.err.println(ip);
			}			
		}
		
		
	}
}


