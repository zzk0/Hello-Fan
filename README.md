# Todo List

功能：
- [x] 超级记忆，科学安排复习的计划。SM2算法。
- [x] 测试模式的匹配要优化，时常写出了大概的样子但是就是不给判对。(备注:目前来看，算法上需要在体验和准确之间做出抉择。当然我选择了体验。)
- [ ] 一繁对多简的学习流程。学习的流程需要优化一下。需要想出一个比较合理的学习方案。(比如，错题要整理起来之类的)
- [ ] 查询字的时候，不管简体还是繁体都可以查到繁体。
- [ ] 抽离HanziView。搞一个库。用户只要引用implementation即可。以后别的项目有需要就可以直接引用了。
- [ ] 阅读界面查字后可以跳转到一个练习的界面，搜索字的同理
- [x] 学习界面的词组的查询需要优化。不单单只是以这个字为首的词组。
- [ ] 设置界面没有做！
- [ ] 将一些在界面的耗时代码改为异步。
- [ ] 各个UI的美化
- [ ] 写后端。加入用户管理。涉及的最主要问题在于数据同步。学习进度的同步，设置选项的同步，历史收藏的同步。主要难点在数据格式的设计。由此，还多了诸如登录注册功能。注销重新登录等诸多功能。新闻的收藏，又涉及服务器要保存新闻，客户端要展示用户收藏的内容。

文档：
- [ ] 项目文档，包括公共的工具类的使用方法
- [ ] UML建模一波。帮助小组的其他人理清楚模块之间的关系。
- [ ] 编码的规范。需要大家一起设计编码的规范，然后规范化自己的代码。

测试：
- [ ] 黑盒测试用例设计，主要针对UI
- [ ] 白盒测试用例设计，主要针对算法。可以使用Junit。
- [ ] 检测App的内存泄漏状况，报告到Bug中。或者提交Issue。(备注：有人建议使用leancanary，但是最新版本和我们的程序使用的一些库的版本不一致，Build不起来)
- [ ] 不同机型的测试工作。每个人build一份最新版本，安装到自己手机。看看哪里有异常。报告到Bug中，或者提交Issue。

打包：
- [ ] 打包成Apk，设计应用的图标。

bug：
- [x] 前一个字是测试模式的情况下，当前字是学习模式的时候，hanziView没有灰色部分。
- [ ] 笔画动画，存在内存泄漏的情况。原因是，匿名内部类持有Activity的context。线程还在继续执行，activity销毁之后，没办法回收，因为Runnable持有了它的context。

# 关于Context的使用

正确使用，以防止内存泄漏。

一般而言，如果context不需要ui相关的操作，就用getApplicationContext()。如果对象存活时间可能比activity长，考虑使用getApplicationContext()。其他情况，确保activity销毁前，取消引用activity，用this就好了。

举个例子：公共的SQLdm这个类，就使用getApplication()，因为不涉及UI。构建AlertDialog，就使用this来获取context。

# 数据库说明

获取一个SQLiteDatabase的对象，具体看这个文件
https://github.com/zhouzekai/Hello-Fan/blob/master/app/src/main/java/com/test/util/SQLdm.java

## words表

说明：这里将原来表里的Json独立出来，方便使用，现在分成了两个表，一个words，一个wordsJson。

![](img/db_words.png)

## wordsJson表

![](img/db_wordsJson.png)

## dict表

![](img/db_dict.png)

## DictHistory表

和dict的表项一直

## s2taW表

![](img/db_s2aw.png)
