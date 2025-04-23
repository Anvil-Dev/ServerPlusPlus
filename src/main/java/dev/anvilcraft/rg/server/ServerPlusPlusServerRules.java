package dev.anvilcraft.rg.server;

import dev.anvilcraft.rg.RollingGateCategories;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.api.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ServerPlusPlusServerRules {
    public static class ViewDistanceValidator extends RGValidator.IntegerValidator {
        @Override
        public @NotNull Map.Entry<Integer, Integer> getRange() {
            return Map.entry(0, 32);
        }
    }
    // 服务器视距
    @Rule(
        allowed = {"0", "12", "16", "32"},
        categories = {
            ServerPlusPlus.MOD_ID,
            RollingGateCategories.CREATIVE
        },
        validator = ViewDistanceValidator.class
    )
    public static int viewDistance = 0;
    // 服务器模拟距离
    @Rule(
        allowed = {"0", "12", "16", "32"},
        categories = {
            ServerPlusPlus.MOD_ID,
            RollingGateCategories.CREATIVE
        },
        validator = ViewDistanceValidator.class
    )
    public static int simulationDistance = 0;
    // 快速发送坐标
    @Rule(
        allowed = {"ops", "true", "false", "1", "2", "3", "4"},
        categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND},
        validator = RGValidator.CommandRuleValidator.class
    )
    public static String commandHere = "ops";
    // 快速定位玩家
    @Rule(
        allowed = {"ops", "true", "false", "1", "2", "3", "4"},
        categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND},
        validator = RGValidator.CommandRuleValidator.class
    )
    public static String commandWhereis = "ops";
    // 待办事项清单
    @Rule(
        allowed = {"ops", "true", "false", "1", "2", "3", "4"},
        categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND},
        validator = RGValidator.CommandRuleValidator.class
    )
    public static String commandTodo = "ops";
    // 地标管理菜单
    @Rule(
        allowed = {"ops", "true", "false", "1", "2", "3", "4"},
        categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND},
        validator = RGValidator.CommandRuleValidator.class
    )
    public static String commandLoc = "ops";
    // 白名单管理
    @Rule(categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND})
    public static boolean commandWlist = false;
    // 封禁名单管理
    @Rule(categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND})
    public static boolean commandBlist = false;
    // 简单获取op
    @Rule(categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND})
    public static boolean commandSop = false;
    // 服务器玩家转移命令
    @Rule(categories = {ServerPlusPlus.MOD_ID, RollingGateCategories.COMMAND})
    public static boolean commandTransfer = false;
    // 快速ping好友
    @Rule(categories = ServerPlusPlus.MOD_ID)
    public static boolean fastPingFriend = false;
    // 欢迎玩家
    @Rule(categories = ServerPlusPlus.MOD_ID)
    public static boolean welcomePlayer = false;
}
