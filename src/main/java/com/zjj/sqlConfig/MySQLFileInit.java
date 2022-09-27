package com.zjj.sqlConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

// 初始数据库的文件
// 也就是,只要在其他模块创建MySQLFileInit类或者调用run方法
// 也就是触发类的加载,静态代码块里就会检查文件是否有指定的数据库连接
// 没有的话就指定准备好的sql文件
// 注
// 因为这个功能很单一（都是static方法）,所以没有进行单例模式的实现
// 想要连接的数据库与sql文件的前缀名要相同
// 数据库配置文件默认是db.properties
// db.properties里各个属性默认是
// jdbc.driver jdbc.url jdbc.username jdbc.password
public class MySQLFileInit {

    public static void run(){
        System.out.println("已初始化完成");
    }

    // 匿名静态代码块，在类加载时只执行一次
    static {
        init();
    }

    // 数据库配置文件的各个参数
    // 要连接的数据库名，sql文件名前缀，注意前两者要保证相等
    private static final String databaseName="ssm_book";
    // 驱动
    private static String driver;
    // 要连接的地址
    private static String url;
    // 用户名
    private static String username;
    // 密码
    private static String password;

    // 初始化连接参数,从配置文件里获得最基本参数
    private static void init() {
        // 先从properties文件获取数据库文件
        Properties properties = new Properties();
        // 数据库配置的文件名
        String configFile = "db.properties";
        // 通过类加载器去读取对应的资源
        try(InputStream inputStream = MySQLFileInit.class.getClassLoader().getResourceAsStream(configFile)){
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        driver = properties.getProperty("jdbc.driver");
        url = properties.getProperty("jdbc.url");
        username = properties.getProperty("jdbc.username");
        password = properties.getProperty("jdbc.password");
        // 看数据库是否在mysql中存在，不存在就创建
        checkDatabase();
    }

    // 如果检查到数据库中没有对应的数据库表
    // 那就向数据库中自动执行sql文件
    private static void checkDatabase()  {
        // 注意将sql文件放在resources文件夹下,核心还是想获取项目目录
        String userDir= String.valueOf(Objects.requireNonNull(MySQLFileInit.class.getResource("/")).getPath());
        // 获取到sql文件的目录
        String path=userDir+databaseName+".sql";
        path=path.substring(1);
        // 查看全部的数据库
        String sql="show databases";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // 在最开始我们连接的是mysql这个数据库,先通过它先来运行命令
        try (Connection connection= DriverManager.getConnection(url.replace(databaseName,"mysql"), username, password)){
            Statement st= connection.createStatement();
            ResultSet rs=st.executeQuery(sql);
            // 列表里面存储的是当前数据库里所有数据库的名字集合
            ArrayList<String> allDatabase=new ArrayList<>();
            while(rs.next())
            {
                allDatabase.add(rs.getString(1));
            }
            // 如果原来的数据库里没有这个数据库
            if (!allDatabase.contains(databaseName)){
                runSqlFile(path);
                // 初始化数据库完成
                System.out.println("初始化数据库完成");
            }else {
                System.out.println("数据库已存在");
            }
            // 数据库准备完毕
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void runSqlFile(String path) throws Exception {
        // 先读取sql文件，使用; 分割存到一个列表里
        ArrayList<String> sqls=readFileByLines(path);
        // 再批量执行
        batchDate(sqls);
    }

    // 批量执行sql文件
    private static void batchDate(ArrayList<String> sqls) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection=DriverManager.getConnection(url.replace(databaseName,"mysql"), username, password)){
            Statement st = connection.createStatement();
            for (String sql : sqls) {
                st.addBatch(sql);
            }
            st.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读取文件,以分号（;）为分割，将sql添加到一个列表里，之后再读取
    private static  ArrayList<String> readFileByLines(String filePath) throws Exception {
        ArrayList<String> listStr=new ArrayList<>();
        StringBuilder sb=new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                // 这里是以utf-8 格式读取的
                Files.newInputStream(Paths.get(filePath)), StandardCharsets.UTF_8))) {
            // 读取指定目录下的文件
            String tempString;
            // 读取文件的基本逻辑是，flag判断缓冲区里面是否还有语句
            // 等于0代表没有
            int flag = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 空行跳过
                if (tempString.trim().equals(""))
                    continue;
                // 注释行，跳过
                if (tempString.trim().startsWith("--") || tempString.trim().startsWith("#")){
                    continue;
                }
                // 结尾是分号
                if (tempString.endsWith(";")) {
                    // 缓冲区还有数据
                    if (flag == 1) {
                        // 加上原来的
                        sb.append(tempString);
                        listStr.add(sb.toString());
                        // 清空缓冲区
                        sb.delete(0, sb.length());
                        flag = 0;
                    }
                    // 没有数据，直接添加
                    else
                        listStr.add(tempString);
                } else {
                    flag = 1;
                    sb.append(tempString);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        // 打印sql语句
        // for (String str:listStr){
        //     System.out.println(str);
        // }
        return listStr;
    }

}
