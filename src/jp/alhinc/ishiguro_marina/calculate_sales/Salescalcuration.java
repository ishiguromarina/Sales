/**
 *
 */
package jp.alhinc.ishiguro_marina.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Salescalcuration {
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}


		//1.支店定義ファイルの読み込み
		HashMap<String, String> branchNameMap = new HashMap<>();
		HashMap<String, Long> branchSaleMap = new HashMap<>();
		if(!fileInput(args[0], "branch.lst", branchNameMap, branchSaleMap, "^[0-9]{3}$", "支店")){
			return;
		}


		//2.商品定義ファイルの読み込み
		HashMap<String, String> commodityNameMap = new HashMap<>();
		HashMap<String, Long> commoditySaleMap = new HashMap<>();

		if(!fileInput(args[0], "commodity.lst", commodityNameMap, commoditySaleMap, "^\\w{8}$", "商品")){
			return;
		}


		//3.集計
		//ファイル入力
		ArrayList<File> rcdlist = new ArrayList<>();
		File dr = new File(args[0]);
		File[] filelist = dr.listFiles();

		BufferedReader br = null;
		try{
			for(int i = 0 ; i < filelist.length ; i++){
				if(filelist[i].isFile() && filelist[i].getName().matches("\\d{8}.rcd")){
					rcdlist.add(filelist[i]);
				}
			}

			//連番処理
			for(int i = 0 ; i < rcdlist.size() - 1; i++){

				//文字列の一部を取得、数値化
				int number = Integer.parseInt(rcdlist.get(i).getName().substring(0, 8));

				//iの次の要素を見る
				int nextnumber = Integer.parseInt(rcdlist.get(i+1).getName().substring(0, 8));

				//if文
				if(nextnumber - number != 1){
					System.out.println("売上ファイル名が連番になっていません");
					return;
				}
			}


			//判定処理
			for(int i = 0 ; i < rcdlist.size(); i++){
				//変数指定
				String branchcode;
				String commoditycode;
				String sales;


				br = new BufferedReader (new FileReader(rcdlist.get(i)));

				if((branchcode = br.readLine()) == null){
					System.out.println(rcdlist.get(i).getName() + "のフォーマットが不正です");
					return;
				}
				if((commoditycode = br.readLine()) == null){
					System.out.println(rcdlist.get(i).getName() + "のフォーマットが不正です");
					return;
				}

				if((sales = br.readLine()) == null){
					System.out.println(rcdlist.get(i).getName() + "の売上金額のフォーマットが不正です");
					return;
				}

				if((br.readLine()) != null){
					System.out.println(rcdlist.get(i).getName() + "のフォーマットが不正です");
					return;
				}

				if(!branchSaleMap.containsKey(branchcode)) {
					System.out.println(rcdlist.get(i).getName() + "の支店コードが不正です");
					return;
				}
				if(!commoditySaleMap.containsKey(commoditycode)) {
					System.out.println(rcdlist.get(i).getName() + "の商品コードが不正です");
					return;
				}

				if(!sales.matches("\\d{1,}")){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}



				//型変換、変数指定
				Long totalbranch = Long.parseLong(sales) + branchSaleMap.get(branchcode);
				Long totalcommodity = Long.parseLong(sales) + commoditySaleMap.get(commoditycode);

				if(totalbranch.toString().length() > 10 || totalcommodity.toString().length() > 10){
					System.out.println("合計金額が10桁を超えました");
					return;
				}

				//マップ代入
				branchSaleMap.put(branchcode, totalbranch);
				commoditySaleMap.put(commoditycode, totalcommodity);

			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}


		//4.集計結果出力
		//branch出力
		if(!fileOutput (args[0], "branch.out", branchNameMap, branchSaleMap)){
			return;
		}

		//commodity出力
		if(!fileOutput (args[0], "commodity.out", commodityNameMap, commoditySaleMap)){
			return;
		}
	}

	//メッソド分け(集計結果出力)
	public static boolean fileOutput(String dirPass, String fileName, HashMap<String, String> nameMap, HashMap<String,Long> saleMap){
		File branchfile = new File(dirPass, fileName);
		BufferedWriter bw = null;

		try{
			bw = new BufferedWriter(new FileWriter(branchfile));

			List<Entry<String,Long>> entries =new ArrayList<Entry<String,Long>>(saleMap.entrySet());
			Collections.sort(entries, new Comparator<Entry<String,Long>>() {

	            public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return (entry2.getValue()).compareTo(entry1.getValue());
	            }
	        });

			for(Entry<String,Long> s : entries) {
				bw.write(s.getKey() + "," + nameMap.get(s.getKey()) + "," + s.getValue());
				bw.newLine();
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(bw != null){
					bw.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}

	//メソッド分け(ファイルの読み込み)
	public static boolean fileInput(String dirPass, String fileName, HashMap<String, String> nameMap, HashMap<String, Long> saleMap, String judgment, String codeName){

		BufferedReader br = null;
		try{
			File file = new File(dirPass, fileName);

	        if(!file.exists()){
	        	System.out.println(codeName + "定義ファイルが存在しません");
	        	return false;
	    	}
			FileReader fr = new FileReader(file);
			br = new BufferedReader (fr);
			String s;
			while((s = br.readLine()) != null){

				String[] data = s.split(",");
				if(data.length != 2 || !data[0].matches(judgment)){
		        	System.out.println(codeName + "定義ファイルのフォーマットが不正です");
		        	return false;
		        }
				nameMap.put(data[0], data[1]);
				saleMap.put(data[0], 0L);

			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}
}

