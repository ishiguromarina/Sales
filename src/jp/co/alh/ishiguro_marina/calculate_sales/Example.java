/**
 *
 */
package jp.co.alh.ishiguro_marina.calculate_sales;

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
import java.util.Map;
import java.util.Map.Entry;

public class Example {
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
		}

//1.支店定義ファイルの読み込み
		HashMap<String,String> branchNameMap = new HashMap<String,String>();
		HashMap<String,Long> branchSaleMap = new HashMap<String,Long>();
		BufferedReader br = null;
		try{
			String fileSeparator = System.getProperty( "file.separator" );
			File file = new File (args[0]+ fileSeparator + "branch.lst");

	        if(!file.exists()){
	        	System.out.println("支店定義ファイルが存在しません");
	        	return;
        	}
			FileReader fr = new FileReader(file);
			br = new BufferedReader (fr);
			String s;
			while((s = br.readLine()) != null){

				String[] branchdata = s.split(",");
				if(!branchdata[0].matches("^[0-9]{3}$") || (branchdata.length != 2)){
		        	System.out.println("支店定義ファイルのフォーマットが不正です");
		        	return;
		        }
				branchNameMap.put(branchdata[0],branchdata[1]);
				branchSaleMap.put(branchdata[0],0L);

			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}



//2.商品定義ファイルの読み込み
		HashMap<String,String> commodityNameMap = new HashMap<String,String>();
		HashMap<String,Long> commoditySaleMap = new HashMap<String,Long>();
		try{
			String fileSeparator = System.getProperty( "file.separator" );
	        //System.out.println( "file.separator=" + fileSeparator );
			File file = new File (args[0]+ fileSeparator + "commodity.lst");

	        if(!file.exists()){
	        	System.out.println("商品定義ファイルが存在しません");
        		return;
	        }
			FileReader fr = new FileReader(file);
			br = new BufferedReader (fr);
			String s;
			while((s = br.readLine()) != null){
				String[] commoditydata = s.split(",");

				if(!commoditydata[0].matches("^\\w{8}$") || (commoditydata.length != 2) ){
		        	System.out.println("商品定義ファイルのフォーマットが不正です");
		        	return;
	        	}
				commodityNameMap.put(commoditydata[0],commoditydata[1]);
				commoditySaleMap.put(commoditydata[0],0L);
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}


//3.集計
	//ファイル入力
		ArrayList<File> rcdlist = new ArrayList<>();
		File dr = new File(args[0]);
		File[] fl = dr.listFiles();

		try{
			for (int i = 0 ; i < fl.length ; i++){
				if(fl[i].getName().matches("\\d{8}.rcd")){
					rcdlist.add(fl[i]);
				}
			}

	//連番処理
			for (int i = 0 ; i < rcdlist.size() - 1; i++){

				//文字列の一部を取得
				String s = rcdlist.get(i).getName();
				String ss = s.substring(0,8);

				//数値化
				int j = Integer.parseInt(ss);

				//iの次の要素を見る
				String next = rcdlist.get(i+1).getName();
				String next1 = next.substring(0,8);
				int k = Integer.parseInt(next1);

				//if文
				if(k - j != 1){
					System.out.println("売り上げファイル名が連番になっていません");
					return;
				}
			}


		//判定処理
			for	(int i = 0 ; i < rcdlist.size(); i++){
				//変数指定
				String branchcode;
				String commoditycode;
				String sales;


					FileReader fr = new FileReader(rcdlist.get(i));
					br = new BufferedReader (fr);

					if((branchcode = br.readLine()) == null){
						System.out.println(rcdlist.get(i).getName()+"の支店コードが不正です");
						return;
					}
					if((commoditycode = br.readLine()) == null){
						System.out.println(rcdlist.get(i).getName()+"の商品コードが不正です");
						return;
					}

					if(!branchSaleMap.containsKey(branchcode)) {
						System.out.println(rcdlist.get(i).getName()+"の支店コードが不正です");
						return;
					}
					if(!commoditySaleMap.containsKey(commoditycode)) {
						System.out.println(rcdlist.get(i).getName()+"の商品コードが不正です");
						return;
					}

					if((sales = br.readLine()) != null){
						branchSaleMap.containsKey(sales);
					}

					if((br.readLine()) != null){
						System.out.println(rcdlist.get(i).getName()+"のフォーマットが不正です");
						return;
					}

		//型変換、変数指定
					Long aaa = Long.parseLong(sales);
					Long totalbranch = aaa + branchSaleMap.get(branchcode);
					Long totalcommodity = aaa + commoditySaleMap.get(commoditycode);


		//マップ代入
					branchSaleMap.put(branchcode,totalbranch);
					commoditySaleMap.put(commoditycode,totalcommodity);

					if(totalbranch >= 10000000000L || totalcommodity >= 10000000000L){
						System.out.println("合計金額が10桁を超えました");
						return;
					}
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			try{
				if(br != null){
					br.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}



//4.集計結果出力
	//branch出力
		String fileSeparator = System.getProperty("file.separator");
		File branchfile = new File(args[0]+ fileSeparator+"branch.out");
		BufferedWriter bw = null;
		try{
			FileWriter fw = new FileWriter (branchfile);
			bw = new BufferedWriter(fw);

			List<Map.Entry<String,Long>> entries =new ArrayList<Map.Entry<String,Long>>(branchSaleMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {

	            public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
	            }
	        });

			for (Entry<String,Long> s : entries) {
			bw.write(s.getKey() + "," + branchNameMap.get(s.getKey()) + "," + s.getValue());
			bw.newLine();
			}
			bw.close();
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}

	//commodity出力
		File commodityfile = new File(args[0]+ fileSeparator+"commodity.out");
		try{
			FileWriter fw = new FileWriter (commodityfile);
			bw = new BufferedWriter(fw);

			List<Map.Entry<String,Long>> entries =new ArrayList<Map.Entry<String,Long>>(commoditySaleMap.entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<String,Long>>() {
	            public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
	                return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
	            }
	        });

			for (Entry<String,Long> s : entries) {
			bw.write(s.getKey() + "," + commodityNameMap.get(s.getKey()) + "," + s.getValue());
			bw.newLine();
			}

		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}finally{
			try{
				if(bw != null){
					bw.close();
				}
			}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
		}
	}
}

