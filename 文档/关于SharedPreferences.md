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
flag | . | .