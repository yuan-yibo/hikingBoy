package com.hiking.infrastructure.ai;

import com.hiking.domain.hiking.entity.HikingRecord;
import com.hiking.domain.share.SocialPlatform;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 本地智能文案生成器
 * 在没有AI API时，根据不同平台风格生成专业文案
 */
@Component
public class LocalContentGenerator {
    
    private final Random random = new Random();
    
    /**
     * 根据平台和记录生成文案
     */
    public String generate(SocialPlatform platform, HikingRecord record) {
        return switch (platform) {
            case XIAOHONGSHU -> generateXiaohongshu(record);
            case MOMENTS -> generateMoments(record);
            case WEIBO -> generateWeibo(record);
        };
    }
    
    /**
     * 生成小红书风格文案
     */
    private String generateXiaohongshu(HikingRecord record) {
        String mountainName = record.getMountainName();
        Double distance = record.getDistance();
        String duration = record.getDuration();
        
        // 标题模板
        List<String> titles = List.of(
            "带娃爬山｜" + mountainName + "亲子徒步攻略！小腿酸但快乐加倍",
            mountainName + "徒步vlog｜" + formatDistance(distance) + "公里挑战成功！宝贝太棒了",
            "周末遛娃｜" + mountainName + "打卡！这条路线绝绝子",
            "亲子户外｜带" + (random.nextBoolean() ? "崽" : "娃") + "征服" + mountainName + "！满满成就感",
            mountainName + "一日游｜和宝贝的" + formatDistance(distance) + "km徒步之旅"
        );
        
        // 正文模板
        List<String> bodies = List.of(
            "✨ 终于带宝贝完成了" + mountainName + "的挑战！\n\n" +
            "📍 地点：" + mountainName + "\n" +
            "📏 里程：" + formatDistance(distance) + "公里\n" +
            "⏱️ 时长：" + nullToDefault(duration, "3小时") + "\n\n" +
            "说实话一开始还担心宝贝能不能坚持下来，结果人家比我还能走！全程自己爬，还一直在说\"妈妈快点\"😂\n\n" +
            "💡 小tips：\n" +
            "1️⃣ 一定要带足水和小零食\n" +
            "2️⃣ 选择早上出发，避开中午暴晒\n" +
            "3️⃣ 穿防滑的鞋子很重要！\n\n" +
            "和孩子一起亲近大自然的感觉真的太棒了！下次还要来挑战更长的路线～",
            
            "🏔️ " + mountainName + "我们来啦！\n\n" +
            "作为一个资深遛娃党，这条路线真的要安利给大家！\n\n" +
            "✅ 路况：整体比较平缓，适合带娃\n" +
            "✅ 距离：" + formatDistance(distance) + "km，对小朋友来说刚刚好\n" +
            "✅ 耗时：" + nullToDefault(duration, "半天") + "左右\n\n" +
            "宝贝今天表现超级棒！一路上叽叽喳喳问个不停，看到什么都好奇～\n\n" +
            "老母亲虽然累趴了，但看到孩子开心的笑脸，一切都值得！\n\n" +
            "周末不知道带娃去哪的姐妹们，冲这里就对了！",
            
            "📸 又解锁一座山！\n\n" +
            "这次带宝贝挑战" + mountainName + "，" + formatDistance(distance) + "公里的路程，小朋友全程自己走完！\n\n" +
            "不得不说，孩子的体力真的是无限的哈哈哈，反而是我们大人先喊累🤣\n\n" +
            "路上遇到好多同样带娃的家庭，大家互相加油打气，氛围超好！\n\n" +
            "💭 带娃户外的意义：\n" +
            "- 让孩子亲近自然，感受四季\n" +
            "- 培养坚持和毅力\n" +
            "- 增进亲子感情\n" +
            "- 家长也能锻炼身体（重点！）\n\n" +
            "一起做个热爱户外的家庭吧～"
        );
        
        String title = titles.get(random.nextInt(titles.size()));
        String body = bodies.get(random.nextInt(bodies.size()));
        
        return "标题：" + title + "\n正文：" + body + "\n\n#亲子徒步 #周末遛娃 #户外亲子 #" + mountainName + " #带娃爬山";
    }
    
    /**
     * 生成朋友圈风格文案
     */
    private String generateMoments(HikingRecord record) {
        String mountainName = record.getMountainName();
        Double distance = record.getDistance();
        String duration = record.getDuration();
        LocalDate date = record.getHikingDate();
        
        String weekday = getWeekday(date);
        
        List<String> templates = List.of(
            "🏔️ " + mountainName + " ✓\n\n" +
            "和小朋友的" + formatDistance(distance) + "公里已达成！\n" +
            "虽然老母亲已经累趴，但宝贝说下次还要来～\n\n" +
            "这就是我向往的周末啊 " + getRandomEmoji(),
            
            weekday + "的快乐，是和宝贝一起爬山🥾\n\n" +
            mountainName + " · " + formatDistance(distance) + "km · " + nullToDefault(duration, "3h") + "\n\n" +
            "每一步都是风景，每一刻都值得记录 ✨",
            
            "📍 " + mountainName + "\n\n" +
            "带娃徒步的第N次打卡～\n" +
            "小朋友说：\"妈妈，我们下次去更高的山！\"\n\n" +
            "好的，安排！💪",
            
            getRandomEmoji() + " 今日成就：" + mountainName + " " + formatDistance(distance) + "公里\n\n" +
            "和宝贝一起走过的路，都变成了最美的回忆。\n\n" +
            "累并快乐着～",
            
            "又是被小朋友体力碾压的一天😂\n\n" +
            mountainName + " | " + formatDistance(distance) + "km\n" +
            "宝贝：so easy～\n" +
            "老母亲：我先躺会...\n\n" +
            "#亲子时光 #周末日常",
            
            "🌿 " + mountainName + "\n\n" +
            "最好的教育，在路上。\n" +
            "最美的风景，有你相伴。\n\n" +
            "今天的" + formatDistance(distance) + "公里，是我们共同的勋章 🏅"
        );
        
        return templates.get(random.nextInt(templates.size()));
    }
    
    /**
     * 生成微博风格文案
     */
    private String generateWeibo(HikingRecord record) {
        String mountainName = record.getMountainName();
        Double distance = record.getDistance();
        String duration = record.getDuration();
        
        List<String> templates = List.of(
            "🏃‍♀️ 打卡" + mountainName + "！\n\n" +
            "今日运动数据：\n" +
            "📍 " + mountainName + "\n" +
            "📏 " + formatDistance(distance) + "公里\n" +
            "⏱️ " + nullToDefault(duration, "3小时") + "\n\n" +
            "带着宝贝一起挑战自然，感受山野的魅力～小朋友全程表现超棒，比我还能走！\n\n" +
            "运动使人快乐，亲子时光更快乐！你们周末都带娃去哪里玩呀？\n\n" +
            "#户外运动# #亲子徒步# #周末打卡# #运动日常# #带娃日记#",
            
            "⛰️ " + mountainName + " 挑战成功！\n\n" +
            formatDistance(distance) + "公里的亲子徒步之旅圆满完成～\n\n" +
            "说真的，和孩子一起爬山的感觉太棒了！一路上看风景、聊天、互相加油，比任何游乐场都有意义。\n\n" +
            "宝贝问我：妈妈，山顶会不会有云彩？\n我说：去了就知道啦～\n\n" +
            "这就是户外的魅力吧，永远充满惊喜和期待！\n\n" +
            "#户外运动# #徒步打卡# #亲子时光# #周末去哪儿# #运动使我快乐#",
            
            "💪 今日份运动已完成！\n\n" +
            mountainName + " × " + formatDistance(distance) + "km × 亲子模式\n\n" +
            "老母亲的腿已经不是自己的了，但是！看到宝贝开心的笑脸，一切都值得～\n\n" +
            "下山的时候小朋友说：妈妈我还能再爬一座！\n" +
            "（内心OS：你行你上，我不行...）\n\n" +
            "周末打卡完毕，明天继续搬砖！\n\n" +
            "#户外运动# #亲子日常# #周末打卡# #徒步爱好者# #运动记录#",
            
            "🌄 解锁新成就：" + mountainName + "！\n\n" +
            "数据播报：\n" +
            "→ 里程：" + formatDistance(distance) + "公里\n" +
            "→ 时长：" + nullToDefault(duration, "半天") + "\n" +
            "→ 队友：我家小神兽\n" +
            "→ 状态：累并快乐着\n\n" +
            "每次和孩子一起户外，都觉得生活充满能量！\n\n" +
            "周末别宅家啦，带上孩子一起感受自然吧～\n\n" +
            "#户外运动# #亲子徒步# #打卡日常# #运动健身# #周末遛娃#"
        );
        
        return templates.get(random.nextInt(templates.size()));
    }
    
    private String formatDistance(Double distance) {
        if (distance == null) return "0";
        return String.format("%.1f", distance);
    }
    
    private String nullToDefault(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
    
    private String getWeekday(LocalDate date) {
        if (date == null) return "周末";
        String[] weekdays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        return weekdays[date.getDayOfWeek().getValue() % 7];
    }
    
    private String getRandomEmoji() {
        String[] emojis = {"✨", "🌟", "💫", "🌈", "🌻", "🌸", "🍀", "⭐"};
        return emojis[random.nextInt(emojis.length)];
    }
}
