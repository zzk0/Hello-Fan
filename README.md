# Todo List

功能：
- [x] 超级记忆，科学安排复习的计划。SM2算法。
- [x] 测试模式的匹配要优化，时常写出了大概的样子但是就是不给判对。(备注:目前来看，算法上需要在体验和准确之间做出抉择。当然我选择了体验。)
- [x] 学习界面的词组的查询需要优化。不单单只是以这个字为首的词组。
- [ ] 一繁对多简的学习流程。目前想到的方案是，那个界面提供三个按钮，分别跳转到练习，错题，收藏。
- [x] 查询字的时候，不管简体还是繁体都可以查到繁体。
- [x] 练习字的界面。查字后跳转到的界面。
- [ ] 目前Json数据不够完整，很多字检索不到。
- [ ] 设置的各个选项的具体功能，比如清理数据，比如设置每天学习的字数。
- [ ] 个人设置信息界面
- [ ] 各个UI的美化
- [ ] 同时支持用户名/手机号登录。使用体验问题，如果忘记用户名，就无法登录了。
- [ ] 想办法解决学习数据同步的问题，主要问题在于如何设计数据格式，如何让前端生成这个格式，后端解析这个格式。
- [ ] 后端：https://github.com/NothingXiang/hello-fan-backend , 目前只有用户注册/登录/找回功能,数据同步等需求未完善，目前只有批量上传/下载words表（这部分功能未提交），有需求可以提；

文档：
- [ ] 项目文档，包括公共的工具类的使用方法
- [ ] UML建模。帮助小组的其他人理清楚模块之间的关系。
- [ ] 编码的规范。需要大家一起设计编码的规范，然后规范化自己的代码。

测试：
- [ ] 黑盒测试用例设计，主要针对UI.可以使用Monkey。检测运行在UI的耗时代码，将他们修改为异步。
- [ ] 白盒测试用例设计，主要针对算法。可以使用Junit。主要测试学习字安排的对不对。
- [x] 检测App的内存泄漏状况，报告到Bug中。或者提交Issue。（备注：目前已经针对Activity做了测试，已经修复了activity的内存泄漏）
- [ ] 检测App的潜在的可能的内存泄漏问题。
- [ ] 不同机型的测试工作。每个人build一份最新版本，安装到自己手机。看看哪里有异常。报告到Bug中，或者提交Issue。
- [ ] 添加日志系统。用于查错。

打包：
- [ ] 打包成Apk，设计应用的图标。

bug：
- [x] 前一个字是测试模式的情况下，当前字是学习模式的时候，hanziView没有灰色部分。
- [x] 笔画动画，存在内存泄漏的情况。原因是，匿名内部类持有Activity的context。线程还在继续执行，activity销毁之后，没办法回收，因为Runnable持有了它的context。
- [x] FindLostActivity, RegisterActivity存在内存泄漏。原因是TimerTask使用了匿名内部类，生命周期长过创建Timer的activity。已经解决。

# 关于Context的使用

正确使用，以防止内存泄漏。

一般而言，如果context不需要ui相关的操作，就用getApplicationContext()。如果对象存活时间可能比activity长，考虑使用getApplicationContext()。其他情况，确保activity销毁前，取消引用activity，用this就好了。

举个例子：公共的SQLdm这个类，就使用getApplication()，因为不涉及UI。构建AlertDialog，就使用this来获取context。

# 关于SQLiteDatabase.close()方法

文档中对方法的说明，简单理解就是，调用这个方法释放资源。
Releases a reference to the object, closing the object if the last reference was released. Calling this method is equivalent to calling releaseReference().

调用的时机，以下博文建议是在onDestroy中。
http://blog.sina.com.cn/s/blog_5de73d0b0102w0g0.html

# 关于测试

测试的目的主要是项目代码质量的把控。

1. 测试界面。主要测试是否因为耗时操作造成界面的卡顿，是否存在闪退。
2. 算法测试。测试算法是否满足项目的需求。
3. 其他。比如内存泄漏的问题。

https://blog.csdn.net/lmj623565791/article/details/79623159
https://juejin.im/post/5b57e3fbf265da0f47352618
https://developer.android.com/studio/test?hl=zh-CN

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

# 关于SharedPreferences的使用

安卓开发中使用SharedPreferences来做少量的数据存储。

!! 切记，请不要存储大量数据到SharePreferences中。

为了避免使用过程中引起冲突，下面统一说明SharedPreferences的使用。

## fan_data

键名 | 样例 | 说明
---- | --- | ---
last_learn_date | 2019-6-30 | 存储上一次学习的时间，作用是判断是否需要开始安排新的一天
current_word | 10 | 存储currentWord。在同一天，如果在LearnWritingActivity退出，再重新点进来，应该继续上一次的学习进度
today_words | 優优3償偿3儲储3... | 今天的学习任务。如果凑不到指定的数目，就从数据库中取。每次结束学习的时候，会根据学习情况更新这个键值。

## setting

键名 | 样例 | 说明
---- | --- | ---
flag | 不详 | 不详
