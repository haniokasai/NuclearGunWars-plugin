package com.haniokasai.nukkit.ArrowGun;

public class lj {

	static String conf_lang = "";
	//teamsys.java
	static String damage_nojoinpvp ="";
	static String damage_attackbyhand = "";
	static String damage_attacksameteam = "";
	static String core_brakemy ="";
	static String core_attacked1 ="";
	static String core_attacked2 ="";
	static String core_win = "";
	static String core_serverrestart ="";
	static String brkhimcore = "";
	static String damage_killfall_1 ="";
	///////////////////

	///gunsys.java
	static String usebu_remaining ="";
	static String usebu_reloadcomp ="";
	static String usebu_relstart1 = "";
	static String usebu_relstart2 ="";
	static String fishingrod_reloading ="";
	static String rekit_comp = "";
	//////////////////

	//Main.java
	static String  cmd_cg_help = "";
	static String  cmd_cg_nogun ="";
	static String  cmd_ch_diff ="";
	static String  cmd_ch_true ="";
	static String cmd_c_inpvp ="";
	static String cmd_pvp_inpvp ="";
	static String cmd_pvp_ts ="";
	static String cmd_pvp_tm = "";
	static String conf_region = "";
	static String cantchat = "";


	/////


	public static void sellang(String ln){
		if(ln.equals("eng")){
			 conf_lang = "eng";
			 conf_region ="2";
			//teamsys.java
			 damage_nojoinpvp = "You can't attack the player not-in PVP.";
			 damage_attackbyhand = "Do not beat the player";
			 damage_attacksameteam = "You can't attack the same team player ";
			 core_brakemy ="attacked Our Team Core.";
			 core_attacked1 =" attacked ";
			 core_attacked2 =" Team's Core.";
			 core_win = "Team Won. Teleport to the respawn point within 10 seconds";
			 core_serverrestart = "Team Won. Server restarts within 20 seconds.";
			 brkhimcore = "Count down of your Banning";
			 damage_killfall_1 =" pushed and killed : ";
			///////////////////

			///gunsys.java
			 usebu_remaining ="Remaining: ";
			 usebu_reloadcomp ="Reload has been completed";
			 usebu_relstart1 = "Started reloading.";
			 usebu_relstart2 =" seconds";
			 fishingrod_reloading ="Reload in...";
			 rekit_comp = "Refit is completed.";
			//////////////////

			//Main.java
			 cmd_cg_help = "/cg  <Gun Num>";
			 cmd_cg_nogun ="There are no gun such a number.";
			 cmd_c_inpvp ="You must in PVP";
			 cmd_pvp_inpvp ="You are already join in PVP";
			 cmd_pvp_ts ="You are in S TEAM";
			 cmd_pvp_tm = "You are in M TEAM";
			 cantchat = "You should check secret key from http://ngw.haniokasai.com,and enter /ch <secret key>";
			 cmd_ch_diff ="Please enter true secret-key";
			 cmd_ch_true ="OK!";

			 ////

		}else if(ln == "kor"){

		}else if(ln.equals("jpn")){
			 conf_lang = "jpn";
			 conf_region ="1";
			//teamsys.java
			 damage_nojoinpvp = "PVPに参加していないプレイヤーは攻撃できません。";
			 damage_attackbyhand = "相手を叩いてはいけません";
			 damage_attacksameteam = "同チームのプレイヤーは攻撃できません。";
			 core_brakemy ="が自チームのコアを攻撃。";
			 core_attacked1 ="が";
			 core_attacked2 ="チームのコアを攻撃。";
			 core_win = "チームが勝ちました。10秒以内にリスポーン地点に移動します";
			 core_serverrestart = "チームが勝ちました。サーバーが20秒以内に再起動します。";
			 brkhimcore = "自チームのコア破壊によるbanのカウントダウン";
			 damage_killfall_1 =" が突き落として倒した: ";
			///////////////////

			///gunsys.java
			 usebu_remaining ="残り弾数: ";
			 usebu_reloadcomp ="リロード完了しました";
			 usebu_relstart1 = "リロード開始しました.";
			 usebu_relstart2 ="秒かかります";
			 fishingrod_reloading ="リロード中です...";
			 rekit_comp = "再装備完了。";
			//////////////////

			//Main.java
			 cmd_cg_help = "/cg  <銃番号>";
			 cmd_cg_nogun ="そのようなidの銃はありません";
			 cmd_c_inpvp =" PVPに参加していないと操作できません";
			 cmd_pvp_inpvp ="あなたはすでにPVPに参加しています";
			 cmd_pvp_ts ="あなたはSチームです";
			 cmd_pvp_tm = "あなたはMチームです";
			 cantchat = "http://ngw.haniokasai.comから、秘密のパスワードを得て、/ch　秘密のパスワードと入力してください";
			 cmd_ch_diff ="秘密のパスワードが違うようです";
			 cmd_ch_true ="登録できました";

		}

	}




}