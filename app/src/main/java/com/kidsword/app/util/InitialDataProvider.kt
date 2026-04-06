package com.kidsword.app.util

import com.kidsword.app.data.model.Word

object InitialDataProvider {
    
    fun getInitialWords(): List<Word> {
        return listOf(
            // Level 1 - 最基础词汇
            // 动物类
            Word(english = "cat", chinese = "猫", phonetic = "/kæt/", category = "动物", level = 1),
            Word(english = "dog", chinese = "狗", phonetic = "/dɒɡ/", category = "动物", level = 1),
            Word(english = "bird", chinese = "鸟", phonetic = "/bɜːd/", category = "动物", level = 1),
            Word(english = "fish", chinese = "鱼", phonetic = "/fɪʃ/", category = "动物", level = 1),
            Word(english = "pig", chinese = "猪", phonetic = "/pɪɡ/", category = "动物", level = 1),
            Word(english = "hen", chinese = "母鸡", phonetic = "/hen/", category = "动物", level = 1),
            
            // 颜色类
            Word(english = "red", chinese = "红色", phonetic = "/red/", category = "颜色", level = 1),
            Word(english = "blue", chinese = "蓝色", phonetic = "/bluː/", category = "颜色", level = 1),
            Word(english = "yellow", chinese = "黄色", phonetic = "/ˈjeləʊ/", category = "颜色", level = 1),
            Word(english = "green", chinese = "绿色", phonetic = "/ɡriːn/", category = "颜色", level = 1),
            Word(english = "white", chinese = "白色", phonetic = "/waɪt/", category = "颜色", level = 1),
            Word(english = "black", chinese = "黑色", phonetic = "/blæk/", category = "颜色", level = 1),
            
            // 数字类
            Word(english = "one", chinese = "一", phonetic = "/wʌn/", category = "数字", level = 1),
            Word(english = "two", chinese = "二", phonetic = "/tuː/", category = "数字", level = 1),
            Word(english = "three", chinese = "三", phonetic = "/θriː/", category = "数字", level = 1),
            Word(english = "four", chinese = "四", phonetic = "/fɔː/", category = "数字", level = 1),
            Word(english = "five", chinese = "五", phonetic = "/faɪv/", category = "数字", level = 1),
            Word(english = "six", chinese = "六", phonetic = "/sɪks/", category = "数字", level = 1),
            
            // Level 2 - 日常用品
            Word(english = "apple", chinese = "苹果", phonetic = "/ˈæpl/", category = "水果", level = 2),
            Word(english = "banana", chinese = "香蕉", phonetic = "/bəˈnɑːnə/", category = "水果", level = 2),
            Word(english = "orange", chinese = "橙子", phonetic = "/ˈɒrɪndʒ/", category = "水果", level = 2),
            Word(english = "book", chinese = "书", phonetic = "/bʊk/", category = "学习用品", level = 2),
            Word(english = "pen", chinese = "钢笔", phonetic = "/pen/", category = "学习用品", level = 2),
            Word(english = "pencil", chinese = "铅笔", phonetic = "/ˈpensl/", category = "学习用品", level = 2),
            Word(english = "bag", chinese = "书包", phonetic = "/bæɡ/", category = "学习用品", level = 2),
            Word(english = "desk", chinese = "桌子", phonetic = "/desk/", category = "家具", level = 2),
            Word(english = "chair", chinese = "椅子", phonetic = "/tʃeə/", category = "家具", level = 2),
            Word(english = "water", chinese = "水", phonetic = "/ˈwɔːtə/", category = "饮品", level = 2),
            Word(english = "milk", chinese = "牛奶", phonetic = "/mɪlk/", category = "饮品", level = 2),
            
            // Level 3 - 身体和家庭
            Word(english = "hand", chinese = "手", phonetic = "/hænd/", category = "身体", level = 3),
            Word(english = "head", chinese = "头", phonetic = "/hed/", category = "身体", level = 3),
            Word(english = "eye", chinese = "眼睛", phonetic = "/aɪ/", category = "身体", level = 3),
            Word(english = "mouth", chinese = "嘴巴", phonetic = "/maʊθ/", category = "身体", level = 3),
            Word(english = "face", chinese = "脸", phonetic = "/feɪs/", category = "身体", level = 3),
            Word(english = "mother", chinese = "妈妈", phonetic = "/ˈmʌðə/", category = "家庭", level = 3),
            Word(english = "father", chinese = "爸爸", phonetic = "/ˈfɑːðə/", category = "家庭", level = 3),
            Word(english = "sister", chinese = "姐妹", phonetic = "/ˈsɪstə/", category = "家庭", level = 3),
            Word(english = "brother", chinese = "兄弟", phonetic = "/ˈbrʌðə/", category = "家庭", level = 3),
            Word(english = "family", chinese = "家庭", phonetic = "/ˈfæməli/", category = "家庭", level = 3),
            Word(english = "home", chinese = "家", phonetic = "/həʊm/", category = "家庭", level = 3),
            
            // Level 4 - 动词和动作
            Word(english = "run", chinese = "跑", phonetic = "/rʌn/", category = "动作", level = 4),
            Word(english = "walk", chinese = "走", phonetic = "/wɔːk/", category = "动作", level = 4),
            Word(english = "eat", chinese = "吃", phonetic = "/iːt/", category = "动作", level = 4),
            Word(english = "drink", chinese = "喝", phonetic = "/drɪŋk/", category = "动作", level = 4),
            Word(english = "sleep", chinese = "睡觉", phonetic = "/sliːp/", category = "动作", level = 4),
            Word(english = "read", chinese = "读", phonetic = "/riːd/", category = "动作", level = 4),
            Word(english = "write", chinese = "写", phonetic = "/raɪt/", category = "动作", level = 4),
            Word(english = "play", chinese = "玩", phonetic = "/pleɪ/", category = "动作", level = 4),
            Word(english = "sing", chinese = "唱歌", phonetic = "/sɪŋ/", category = "动作", level = 4),
            Word(english = "dance", chinese = "跳舞", phonetic = "/dɑːns/", category = "动作", level = 4),
            
            // Level 5 - 形容词和描述
            Word(english = "big", chinese = "大的", phonetic = "/bɪɡ/", category = "形容词", level = 5),
            Word(english = "small", chinese = "小的", phonetic = "/smɔːl/", category = "形容词", level = 5),
            Word(english = "happy", chinese = "开心的", phonetic = "/ˈhæpi/", category = "形容词", level = 5),
            Word(english = "sad", chinese = "伤心的", phonetic = "/sæd/", category = "形容词", level = 5),
            Word(english = "good", chinese = "好的", phonetic = "/ɡʊd/", category = "形容词", level = 5),
            Word(english = "bad", chinese = "坏的", phonetic = "/bæd/", category = "形容词", level = 5),
            Word(english = "hot", chinese = "热的", phonetic = "/hɒt/", category = "形容词", level = 5),
            Word(english = "cold", chinese = "冷的", phonetic = "/kəʊld/", category = "形容词", level = 5),
            Word(english = "fast", chinese = "快的", phonetic = "/fɑːst/", category = "形容词", level = 5),
            Word(english = "slow", chinese = "慢的", phonetic = "/sləʊ/", category = "形容词", level = 5),
            
            // Level 6 - 进阶词汇
            Word(english = "school", chinese = "学校", phonetic = "/skuːl/", category = "场所", level = 6),
            Word(english = "teacher", chinese = "老师", phonetic = "/ˈtiːtʃə/", category = "人物", level = 6),
            Word(english = "friend", chinese = "朋友", phonetic = "/frend/", category = "人物", level = 6),
            Word(english = "sunny", chinese = "晴朗的", phonetic = "/ˈsʌni/", category = "天气", level = 6),
            Word(english = "rainy", chinese = "下雨的", phonetic = "/ˈreɪni/", category = "天气", level = 6),
            Word(english = "windy", chinese = "刮风的", phonetic = "/ˈwɪndi/", category = "天气", level = 6),
            Word(english = "today", chinese = "今天", phonetic = "/təˈdeɪ/", category = "时间", level = 6),
            Word(english = "tomorrow", chinese = "明天", phonetic = "/təˈmɒrəʊ/", category = "时间", level = 6),
            Word(english = "week", chinese = "周", phonetic = "/wiːk/", category = "时间", level = 6),
            Word(english = "month", chinese = "月", phonetic = "/mʌnθ/", category = "时间", level = 6)
        )
    }
    
    fun getCategories(): List<String> {
        return listOf("动物", "颜色", "数字", "水果", "学习用品", "家具", "饮品", 
                     "身体", "家庭", "动作", "形容词", "场所", "人物", "天气", "时间")
    }
    
    fun getLevels(): List<Int> {
        return listOf(1, 2, 3, 4, 5, 6)
    }
}
