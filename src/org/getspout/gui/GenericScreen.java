package org.getspout.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.getspout.BukkitContrib;
import org.getspout.packet.PacketWidget;
import org.getspout.player.ContribCraftPlayer;

public abstract class GenericScreen extends GenericWidget implements Screen{
	protected List<Widget> widgets = new ArrayList<Widget>();
	protected int playerId;
	public GenericScreen() {
		
	}
	
	public GenericScreen(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public Widget[] getAttachedWidgets() {
		Widget[] list = new Widget[widgets.size()];
		widgets.toArray(list);
		return list;
	}

	@Override
	public Screen attachWidget(Widget widget) {
		widgets.add(widget);
		widget.setScreen(this);
		return this;
	}

	@Override
	public Screen removeWidget(Widget widget) {
		widgets.remove(widget);
		boolean visibility = widget.isVisible();
		widget.setVisible(false);
		((ContribCraftPlayer)BukkitContrib.getPlayerFromId(playerId)).sendPacket(new PacketWidget(widget, getId()));
		widget.setScreen(null);
		widget.setVisible(visibility );
		return this;
	}
	
	@Override
	public boolean containsWidget(Widget widget) {
		return containsWidget(widget.getId());
	}
	
	@Override
	public boolean containsWidget(UUID id) {
		return getWidget(id) != null;
	}
	
	@Override
	public Widget getWidget(UUID id) {
		for (Widget w : widgets) {
			if (w.getId().equals(id)) {
				return w;
			}
		}
		return null;
	}
	
	@Override
	public boolean updateWidget(Widget widget) {
		int index = widgets.indexOf(widget);
		if (index > -1) {
			widgets.remove(index);
			widgets.add(index, widget);
			return true;
		}
		return false;
	}
	
	@Override
	public void onTick() {
		ContribCraftPlayer player = (ContribCraftPlayer)BukkitContrib.getPlayerFromId(playerId);
		if (player != null && player.getVersion() > 17) {
			for (Widget widget : widgets) {
				widget.onTick();
				if (widget.isDirty()) {
					player.sendPacket(new PacketWidget(widget, getId()));
					widget.setDirty(false);
				}
			}
		}
	}
	
	@Override
	public void render() {}
}