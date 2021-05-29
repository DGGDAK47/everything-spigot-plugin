package org.ultragore.everything.modules.TabList;

import java.util.ArrayList;
import java.util.List;

import org.ultragore.everything.types.Module;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class TabList extends Module {

	@Override
	public void enableModule() {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		
		
		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
			@Override
			public void onPacketSending(PacketEvent event) {
				
				PacketContainer currentPacket = event.getPacket();
				StructureModifier<List<PlayerInfoData>> currentStructure = currentPacket.getPlayerInfoDataLists();
				
				List<PlayerInfoData> playersData = currentStructure.getValues().get(0);
				System.out.println("-----------------");
				for(PlayerInfoData pData: playersData) {
					
				}
				
				
				
				PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
				StructureModifier<List<PlayerInfoData>> structure = packet.getPlayerInfoDataLists();
				
				List<PlayerInfoData> list = new ArrayList<PlayerInfoData>();
				list.add(new PlayerInfoData(new WrappedGameProfile("1111111111", "LOH"), 500, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText("LOH")));
				list.add(new PlayerInfoData(new WrappedGameProfile("111111111111", "PIDOR"), 500, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText("PIDOR")));
				list.add(new PlayerInfoData(new WrappedGameProfile("111111111111111", "HUI"), 500, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText("HUI")));
				
				structure.writeSafely(0, list);
				
				event.setPacket(packet);
			}
		});
	}

	@Override
	public void disableModule() {
		
	}

}
