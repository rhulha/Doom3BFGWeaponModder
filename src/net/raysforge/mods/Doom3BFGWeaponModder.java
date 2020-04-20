package net.raysforge.mods;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenu;

import net.raysforge.commons.StreamUtils;
import net.raysforge.easyswing.EasySwing;
import net.raysforge.easyswing.EasyTable;

public class Doom3BFGWeaponModder implements ActionListener {

	private static final String TABLE_NAME_FIRE_RATE = "fire rate";
	private static final String TABLE_NAME_RELOAD_RATE = "reload rate";
	private static final String TABLE_NAME_CLIP_SIZE = "clip size";
	private static final String TABLE_NAME_DAMAGE = "damage";

	String Doom3BFGDirectory = "D:\\Action\\GOGalaxy\\Games\\DOOM 3 BFG\\";
	
	String resource_path = "/net/raysforge/mods";
	
	String pi_def = "/def/weapon_pistol.def";
	String mg_def = "/def/weapon_machinegun.def";
	String sg_def = "/def/weapon_shotgun.def";
	
	String pi_script = "/script/weapon_pistol.script";
	String mg_script = "/script/weapon_machinegun.script";
	String sg_script = "/script/weapon_shotgun.script";

	String clipSize = "%CLIP_SIZE%";
	String damage = "%DAMAGE%";
	String fireRate = "%FIRE_RATE%";
	String reloadRate = "%RELOAD_RATE%";

	private EasyTable easyTable;

	private EasySwing easySwing;

	private HashMap<String, String> tableValues;
	

	public Doom3BFGWeaponModder() {
		easySwing = new EasySwing("Doom3BFGWeaponModder", 1024, 768);
		JMenu fileMI = easySwing.addMenuItem("File");
		easySwing.addMenuItem(fileMI, "Run", "run", this);
		
		easySwing.addToolBarItem("Run Doom3BFG", "run", this);
		easySwing.addToolBarItem("Mod Doom3BFG", "mod", this);
		easySwing.addToolBarItem("This mod always enables auto shotgun");
		
		easyTable = easySwing.setTableAsMainContent();
		easyTable.addColumn("Property");
		easyTable.addColumn("Default");
		easyTable.addColumn("Value");

		easyTable.addRow("pistol damage", "14", "14");
		easyTable.addRow("pistol fire rate", "0.4", "0.1");
		easyTable.addRow("pistol clip size", "12", "14");

		easyTable.addRow("machinegun damage", "9", "9");
		easyTable.addRow("machinegun fire rate", "0.1", "0.08");
		easyTable.addRow("machinegun clip size", "60", "80");

		easyTable.addRow("shotgun damage", "14", "14");
		easyTable.addRow("shotgun num projectiles", "13", "13");
		easyTable.addRow("shotgun fire rate", "1.333", "0.1");
		easyTable.addRow("shotgun clip size", "8", "18");
		easyTable.addRow("shotgun reload rate", "2", "6");

		easySwing.show();
	}

	public static void main(String[] args) {

		new Doom3BFGWeaponModder();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand().equals("mod")) {
			
			tableValues = new HashMap<>(); 
			
			int rowCount = easyTable.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				String key = easyTable.getValue(i, 0);
				String value = easyTable.getValue(i, 2);
				tableValues.put(key, value);
			}
			
			replaceValuesInDefFile(pi_def, "pistol");
			replaceValuesInScriptFile(pi_script, "pistol");

			replaceValuesInDefFile(mg_def, "machinegun");
			replaceValuesInScriptFile(mg_script, "machinegun");

			replaceValuesInDefFile(sg_def, "shotgun");
			replaceValuesInScriptFile(sg_script, "shotgun");

		}
		
		if (e.getActionCommand().equals("run")) {

			ArrayList<String> cmds = new ArrayList<>();
			cmds.add(Doom3BFGDirectory + "Doom3BFG.exe");
			cmds.add("+set");
			cmds.add("com_skipIntroVideos");
			cmds.add("1");
			cmds.add("+set");
			cmds.add("fs_resourceLoadPriority");
			cmds.add("0");
			cmds.add("+set");
			cmds.add("com_showFPS");
			cmds.add("0");

			try {
				Runtime.getRuntime().exec(cmds.toArray(new String[0]), null, new File(Doom3BFGDirectory));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void replaceValuesInDefFile(String filename, String weapon) {
		InputStream is = this.getClass().getResourceAsStream(resource_path+filename);
		try {
			String data = StreamUtils.readCompleteInputStream(is, "ISO-8859-1");
			
			data = data.replace(damage, tableValues.get(weapon + " " + TABLE_NAME_DAMAGE));
			data = data.replace(clipSize, tableValues.get(weapon + " " + TABLE_NAME_CLIP_SIZE));
		
			FileWriter fw = new FileWriter(Doom3BFGDirectory + "base\\" + filename);
			fw.write(data);
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void replaceValuesInScriptFile(String filename, String weapon) {
		InputStream is = this.getClass().getResourceAsStream(resource_path+filename);
		try {
			String data = StreamUtils.readCompleteInputStream(is, "ISO-8859-1");
			
			data = data.replace(fireRate, tableValues.get(weapon + " " + TABLE_NAME_FIRE_RATE));
			if(  tableValues.get(weapon + " " + TABLE_NAME_RELOAD_RATE) != null )
				data = data.replace(reloadRate, tableValues.get(weapon + " " + TABLE_NAME_RELOAD_RATE));
			
			FileWriter fw = new FileWriter(Doom3BFGDirectory + "base\\" + filename);
			fw.write(data);
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
