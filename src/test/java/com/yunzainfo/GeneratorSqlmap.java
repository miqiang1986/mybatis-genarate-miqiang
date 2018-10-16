package com.yunzainfo;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * 生成mybatis相关文件启动类
 * @author： miqiang
 * @date： 2018/09/05
 * version：     1.0
 */
public class GeneratorSqlmap {

	// 配置文件路径
	private final String path = System.getProperty("user.dir") + File.separator +"src" + File.separator + "test" + File.separator + "resourse";
	// xml配置文件路径
	private final String xmlName = "generatorConfig.xml";
	// config配置文件名称
	private final String propName = "config.properties";
	// 实体类存放路径
    private String entityPath;
	// 要替换xml配置文件中的表信息字符串
	private final String tableRaleaseStr = "<table tableName=\"example\"/>";
	// where条件查询，全部默认为true开启
	Map<String, Boolean> exampleMap = new HashMap<String, Boolean>();
	{
		// 1. 设置where条件查询，全部默认为true开启
		// 1.1 Count语句中加入where条件查询，默认为true开启
		exampleMap.put("enableCountByExample", false);
		// 1.2 Update语句中加入where条件查询，默认为true开启
		exampleMap.put("enableUpdateByExample", false);
		// 1.3 Delete语句中加入where条件查询，默认为true开启
		exampleMap.put("enableDeleteByExample", false);
		// 1.4 Select多条语句中加入where条件查询，默认为true开启
		exampleMap.put("enableSelectByExample", false);
		// 1.5 Select单个对象语句中加入where条件查询，默认为true开启
		exampleMap.put("selectByExampleQueryId", false);
	}

	/**
	 * 常修改参数
	 */
	// 文件路径信息
	Map<String, String> pathMap = new HashMap<String, String>();
	{
		// 生成的实体类存放目录
        entityPath = "com.yunzainfo.eagle.finch.footer.entity";
		pathMap.put("entityfile.package", entityPath);
		// 生成的mapper接口文件存放目录
		pathMap.put("xmlmapperfile.package", "com.yunzainfo.eagle.finch.footer.mapper");
		// 生成的xml文件存放目录
		pathMap.put("xmlfile.package", "main/resources/footer");
	}
	// 数据库表和实体类对应。key为数据库表，value为实体类名称
	Map<String, String> tableMap = new HashMap<String, String>();
	{
		// 设置数据库表和实体类对应.key为数据库表，value为实体类名称
		tableMap.put("tab_jsxx", "Jsxx");
	}

	/**
	 * 替换字符串中的指定字符串为新字符串
	 * @param oldStr		原字符串
	 * @param replaceStr	被替换的字符串
	 * @param newStr		要替换成的新字符串
	 * @param replaceAll	是否替换所有相同的字符串
	 * @return
	 * 		替换后的字符串
	 * 		当新字符串的长度比被替换的字符串长度小的时候，在最终字符串后面添加空格
	 */
	public static String replaceStr(String oldStr, String replaceStr, String newStr, Boolean replaceAll) {
		if (oldStr.contains(replaceStr)) {
			StringBuilder sb = new StringBuilder();
			int start = oldStr.indexOf(replaceStr);
			sb.append(oldStr.substring(0, start));
			sb.append(newStr);
			sb.append(oldStr.substring(start + replaceStr.length()));
			if (replaceAll) {
				return replaceStr(sb.toString(), replaceStr, newStr, replaceAll);
			}
			return sb.toString();
		}
		return oldStr;
	}

	/**
	 * 替换字符串中的指定字符串为新字符串
	 * @param oldStr		原字符串
	 * @param map			key:被替换的字符串,value:要替换成的新字符串
	 * @param replaceAll	是否替换所有相同的字符串
	 * @return
	 * 		替换后的字符串
	 * 		当新字符串的长度比被替换的字符串长度小的时候，在最终字符串后面添加空格
	 */
	public static String replaceStr(String oldStr, Map<String, String> map, Boolean replaceAll){
		Set<String> keys = map.keySet();
		for (String key : keys){
			if (oldStr.contains(key)) {
				oldStr = replaceStr(oldStr, key, map.get(key), replaceAll);
			}
		}
		return oldStr;
	}

	/**
	 * 替换文件中的指定字符串
	 * @param path		文件路径
	 * @param fileName	文件名称
	 * @param map		key:被替换的字符串,value:要替换成的新字符串
	 */
	public static void replaceFileStr(String path, String fileName, Map<String, String> map){
		File oldFile = new File(path+File.separator+fileName);
		File newFile = new File(path+File.separator+"1"+fileName);
		BufferedReader bis = null;
		FileWriter bos = null;
		String s = null;
		try {
			bis = new BufferedReader(new FileReader(oldFile));
			bos = new FileWriter(newFile);
			while(null != (s = bis.readLine())) {
				s = replaceStr(s, map, true);
				s= s+"\r\n";
				bos.write(s);
			}
			bos.close();
			bis.close();
			s = null;
			oldFile.delete();
			newFile.renameTo(new File(path+File.separator+fileName));
		} catch(FileNotFoundException e) {
			System.out.println("未找到文件");
		} catch(IOException ee) {
			System.out.println("aaa");
		}
	}

	/**
	 * 生成mybatis相关文件
	 * @throws Exception
	 */
	public void generator() throws Exception {
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		// 指定 逆向工程配置文件
		File configFile = new File(path+File.separator+xmlName);
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}

	/**
	 * 替换xml文件中的指定字符串
	 * @throws ConfigurationException properties文件读取异常
	 */
	public void replaceXmlStr() throws ConfigurationException {
		// properties配置文件
		PropertiesConfiguration config = new PropertiesConfiguration(path + File.separator + propName);
		Iterator<String> keys = config.getKeys();
		Map<String, String> map = new HashMap<String, String>();
		while (keys.hasNext()){
			String key = keys.next();
			map.put("${" + key + "}", config.getString(key));
		}
		// 文件路径信息
		Set<String> pathKeys = pathMap.keySet();
		for (String pathKey : pathKeys){
			map.put("${" + pathKey + "}", pathMap.get(pathKey));
		}
		// 组装table配置标签
		StringBuilder tabStr = new StringBuilder("\n");
		Set<String> tabKeys = tableMap.keySet();
		for (String tabKey : tabKeys){
			tabStr.append("\t\t<table tableName=\"" + tabKey + "\" domainObjectName=\"" + tableMap.get(tabKey) + "\" ");
			Set<String> exampleKeys = exampleMap.keySet();
			for (String exampleKey : exampleKeys){
				tabStr.append(" " + exampleKey + "=\"" + exampleMap.get(exampleKey) + "\" ");
			}
			tabStr.append(" />\n");
		}
		map.put(tableRaleaseStr, tabStr.toString());
		replaceFileStr(path, xmlName, map);
	}

	public static void main(String[] args){
		try {
			System.out.println("开始生成，请稍后...");
			GeneratorSqlmap generatorSqlmap = new GeneratorSqlmap();
			System.out.println("正在创建配置文件...");
			File xmlFile = new File(generatorSqlmap.path+File.separator+generatorSqlmap.xmlName);
			if (xmlFile.exists()){
				xmlFile.delete();
			}
			Files.copy(new File(generatorSqlmap.path+File.separator+"generatorConfig-example.xml").toPath(),
					xmlFile.toPath());
			// 将config配置文件中的值和table配置信息替换到xml中
			generatorSqlmap.replaceXmlStr();
			// 执行生成
			System.out.println("正在生成文件...");
			generatorSqlmap.generator();
			// 删除复制出来的xml配置文件
//			xmlFile.delete();
			System.out.println("生成完成！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
