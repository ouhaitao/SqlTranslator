# SQL转译

**目前仅初步完成整体架构以及提供一个简单的Mysql解析器与转译器**

保持程序原数据库语法不变,通过`解析器-通用语法树-转译器`将原SQL转译成新数据库的SQL。
在程序更改数据库产品时,如果已存在对应的转译器,将转译器配置到程序当中即可立即实现数据库的切换,只须修改非常少量的程序代码。
如果不存在,则只需要实现对应的转译器即可。

其中核心组件是`解析器与转译器`,解析器将SQL解析成`通用语法树`,转译器将`通用语法树`转译成SQL。
访问语法树是以`访问者`的形式,在适配语法的开发中,只需要新增一个`语法树节点类型`然后实现对应的访问方法即可。

# 程序接入注意事项
1. 如果是项目中手动添加的PagHelper拦截器,则需要先添加sqlTranslator拦截器,以确保sqlTranslator拦截器后于PageHelper拦截器执行,
如果使用的是PageHelper-spring-boot-stater,则sqlTranslator在创建拦截器时会自动将其添加到mybatis,PageHelper在afterProperties方法中才会添加到mybatis。
2. 达梦SQL中的表名、字段等,如果没有使用引号,则在执行时会默认转换成大写。并且达梦是大小写敏感数据库,所以如果表字段是小写,但是SQL没有使用引号则会报错找不到字段,所以转译拦截器会默认将表名、表字段转换成大写。在新建表、字段时,应该全部大写。
3. PageHelper与sqlTranslator同样使用了jsqlparser,sqlTranslator中使用的是4.4版本,现在项目中使用的PageHelper是5.1,PageHelper-spring-boot-stater是1.2.3,他们使用的都是1.0的jsqlparser,会报错找不到方法。 所以需要pageHelper升级到5.3.3,PageHelper-spring-boot-stater升级到1.4.7。
4. PageHelper的方言不设置或者使用PageHelper方言,只有PageHelper方言才会生效。PageHelper会根据jdbc的url判断使用合种方言,我们的sql是mysql语法,jdbc是dm等其他数据库,所以需要向PageAutoDialect添加方言映射,在sqlTranslator创建翻译拦截器时会向PageAutoDialect添加dm与MysqlDialect映射。
5. 日志中需要打印`MybatisTranslateInterceptorAutoConfiguration`中的日志表示创建成功