package com.chw.test.config;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 反向工程，将表结构转为对象，自动生成controller,service,entity,mapper等对象
 * @author CarlBryant
 * @since  2019/3/18 14:48
 * @version 1.0
 **/
public class CodeGenerator {

    //private static String modulePath="";
    private static final String modulePath="/independent-project";
    private static final String configPath = "/src/main/resources/application-dev.properties";
    private static String[] tableName = new String[] {"sys_user"};

    /**
     * 作者
     */
    private static String author;

    /**
     * 数据库
     */
    private static String url;
    private static String driverName;
    private static String userName;
    private static String password;

    private static String projectPath;

    /**
     * 代码生成的目录
     */
    private static String packageName;
    private static String outputPath;
    private static String mapperXmlPath;

    public static void main(String[] args) {

        /**
         * 初始化变量
         */
        initial();

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();

        gc.setOutputDir(projectPath + outputPath);
        //如果文件存在是否覆盖，覆盖有风险，最好不要覆盖
        gc.setFileOverride(false);
        gc.setActiveRecord(true);
        // XML 二级缓存
        gc.setEnableCache(false);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(false);
        gc.setAuthor(author);
        gc.setOpen(false);

        //生成文件名:
        gc.setXmlName("%sMapper");
        gc.setMapperName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        dsc.setDriverName(driverName);
        dsc.setUsername(userName);
        dsc.setPassword(password);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(packageName);
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return projectPath + mapperXmlPath + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setTemplate(new TemplateConfig().setXml(null));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();

        /**
         *设置表名前缀；
         *此表名前缀若与数据库表名前缀一致，则自动创建实体类时，实体类名匹配数据库表名下划线后面的名；
         *栗子：数据库表名为 admin_info ，当strategy.setTablePrefix("admin_")时，则实体类名为Info；
         *若不设置表名前缀，则实体类名匹配数据库表名
         */
        strategy.setTablePrefix( "bus_", "com_", "res_","sys_");

        /**
         * 需要生成代码的表的设置
         * 若不设置则会生成全表的代码
         */
        strategy.setInclude(tableName);
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();

    }


    private static void initial() {
        try{

            projectPath = System.getProperty("user.dir")+modulePath;

            Yaml yaml = new Yaml();// 这个需要的jar为:org.yaml.snakeyaml

            //CodeGenerator 这个是这个主函数所在的类的类名
            InputStream resourceAsStream = CodeGenerator.class.getClassLoader().getResourceAsStream("application.yml");

            //加载流,获取yaml文件中的配置数据，然后转换为Map，
            Map<String,Object> obj =  yaml.load(resourceAsStream);

            author = getValueByPath(obj,"code.generator-author");
            url = getValueByPath(obj,"spring.datasource.url");
            driverName = getValueByPath(obj,"spring.datasource.driver-class-name");
            userName = getValueByPath(obj,"spring.datasource.username");
            password = getValueByPath(obj,"spring.datasource.password");
            outputPath = getValueByPath(obj,"code.generator-output-path");
            packageName = getValueByPath(obj,"code.generator-package-name");
            mapperXmlPath = getValueByPath(obj,"code.generator-mapper-xml-path");

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String getValueByPath(Map<String,Object> obj,String path){
        String[] split = path.split("\\.");
        for (int i = 0; i < split.length-1; i++) {
            obj= (Map<String, Object>) obj.get(split[i]);
        }
        return (String) obj.get(split[split.length-1]);
    }



}