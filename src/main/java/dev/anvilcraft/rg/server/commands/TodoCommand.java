package dev.anvilcraft.rg.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.anvilcraft.rg.api.RGValidator;
import dev.anvilcraft.rg.api.server.TranslationUtil;
import dev.anvilcraft.rg.server.ServerPlusPlusServerRules;
import dev.anvilcraft.rg.tools.FilesUtil;
import dev.anvilcraft.rg.tools.IdGenerator;
import dev.anvilcraft.rg.tools.ModCommands;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TodoCommand {
    public static final FilesUtil.MapFile<Long, Todo> TODO = new FilesUtil.MapFile<>("todo", Long::decode, Todo.class);

    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            ModCommands.root(dispatcher, "todo")
                .requires(stack -> RGValidator.CommandRuleValidator.hasPermission(() -> ServerPlusPlusServerRules.commandTodo, stack))
                .executes(TodoCommand::list)
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("desc", StringArgumentType.greedyString())
                                .executes(TodoCommand::add)
                        )
                )
                .then(
                    Commands.literal("remove")
                        .then(
                            Commands.argument("id", LongArgumentType.longArg())
                                .suggests(TodoCommand::suggestId)
                                .executes(TodoCommand::remove)
                        )
                )
                .then(
                    Commands.literal("list")
                        .executes(TodoCommand::list)
                        .then(
                            Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(TodoCommand::list)
                        )
                )
                .then(
                    Commands.literal("success")
                        .then(
                            Commands.argument("id", LongArgumentType.longArg())
                                .suggests(TodoCommand::suggestId)
                                .executes(TodoCommand::success)
                                .then(
                                    Commands.argument("success", BoolArgumentType.bool())
                                        .executes(TodoCommand::success)
                                )
                        )
                )
        );
    }

    private static @NotNull CompletableFuture<Suggestions> suggestId(
        final CommandContext<CommandSourceStack> context,
        final SuggestionsBuilder builder
    ) {
        return SharedSuggestionProvider.suggest(TODO.map.keySet().stream().map(Object::toString), builder);
    }


    public static int add(CommandContext<CommandSourceStack> context) {
        TODO.init(context);
        CommandSourceStack source = context.getSource();
        long id = IdGenerator.nextId();
        String desc = StringArgumentType.getString(context, "desc");
        TODO.map.put(id, new Todo(id, desc, false));
        TODO.save();
        source.sendSuccess(() -> TranslationUtil.trans("command_todo.message.added", desc), false);
        return 1;
    }

    public static int remove(CommandContext<CommandSourceStack> context) {
        TODO.init(context);
        Long id = LongArgumentType.getLong(context, "id");
        Todo todo = TODO.map.remove(id);
        if (todo == null) {
            context.getSource().sendFailure(TranslationUtil.trans("command_todo.message.no_such_id", id));
            return 0;
        }
        TODO.save();
        context.getSource().sendSuccess(() -> TranslationUtil.trans("command_todo.message.removed", todo.desc), false);
        return 1;
    }

    public static int success(CommandContext<CommandSourceStack> context) {
        TODO.init(context);
        Long id = LongArgumentType.getLong(context, "id");
        boolean success;
        try {
            success = BoolArgumentType.getBool(context, "success");
        } catch (IllegalArgumentException ignored) {
            success = true;
        }
        Todo todo = TODO.map.get(id);
        if (todo == null) {
            context.getSource().sendFailure(TranslationUtil.trans("command_todo.message.no_such_id", id));
            return 0;
        }
        todo.success = success;
        TODO.save();
        boolean finalSuccess = success;
        context.getSource().sendSuccess(() -> TranslationUtil.trans(
            "command_todo.message.success",
            todo.desc,
            finalSuccess ? TranslationUtil.trans("command_todo.message.done") : TranslationUtil.trans("command_todo.message.undone")
        ), false);
        return 1;
    }

    public static int list(CommandContext<CommandSourceStack> context) {
        TODO.init(context);
        int page;
        try {
            page = IntegerArgumentType.getInteger(context, "page");
        } catch (IllegalArgumentException ignored) {
            page = 1;
        }
        final int pageSize = 8;
        int size = TODO.map.size();
        int maxPage = size / pageSize + 1;
        if (page > maxPage) {
            context.getSource().sendFailure(TranslationUtil.trans("command_todo.message.no_such_page", page));
            return 0;
        }
        Todo[] todos = TODO.map.values().toArray(new Todo[0]);
        context.getSource().sendSystemMessage(
            TranslationUtil.trans("command_todo.message.page_title", page, maxPage)
                .withStyle(ChatFormatting.YELLOW)
        );
        for (int i = (page - 1) * pageSize; i < size && i < page * pageSize; i++) {
            context.getSource().sendSystemMessage(TodoToComponent(todos[i]));
        }
        Component prevPage = page <= 1 ?
            Component.literal("<<<").withStyle(ChatFormatting.GRAY) :
            Component.literal("<<<").withStyle(
                Style.EMPTY
                    .applyFormat(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent.RunCommand("/todo list " + (page - 1)))
            );
        Component nextPage = page >= maxPage ?
            Component.literal(">>>").withStyle(ChatFormatting.GRAY) :
            Component.literal(">>>").withStyle(
                Style.EMPTY
                    .applyFormat(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent.RunCommand("/todo list " + (page + 1)))
            );
        context.getSource().sendSystemMessage(
            Component.literal("=======")
                .withStyle(ChatFormatting.YELLOW)
                .append(" ")
                .append(prevPage)
                .append(" ")
                .append(TranslationUtil.trans("command_todo.message.page_footer", page, maxPage).withStyle(ChatFormatting.YELLOW))
                .append(" ")
                .append(nextPage)
                .append(" ")
                .append(Component.literal("=======").withStyle(ChatFormatting.YELLOW))
        );
        return 1;
    }

    private static @NotNull MutableComponent TodoToComponent(@NotNull Todo todo) {
        MutableComponent component = Component.literal(todo.desc).withStyle(
            Style.EMPTY
                .withStrikethrough(todo.success)
                .applyFormat(ChatFormatting.GRAY)
                .withHoverEvent(new HoverEvent.ShowText(Component.literal(Long.toString(todo.id))))
        );
        MutableComponent success = Component.literal("[✔]").withStyle(
            Style.EMPTY
                .applyFormat(ChatFormatting.GREEN)
                .withHoverEvent(new HoverEvent.ShowText(TranslationUtil.trans("command_todo.message.make_done")))
                .withClickEvent(new ClickEvent.RunCommand("/todo success %s".formatted(todo.id)))
        );
        MutableComponent unSuccess = Component.literal("[❌]").withStyle(
            Style.EMPTY
                .applyFormat(ChatFormatting.RED)
                .withHoverEvent(new HoverEvent.ShowText(TranslationUtil.trans("command_todo.message.make_undone")))
                .withClickEvent(new ClickEvent.RunCommand("/todo success %s false".formatted(todo.id)))
        );
        MutableComponent remove = Component.literal("[\uD83D\uDDD1]").withStyle(
            Style.EMPTY
                .applyFormat(ChatFormatting.RED)
                .withHoverEvent(new HoverEvent.ShowText(TranslationUtil.trans("command_todo.message.remove")))
                .withClickEvent(new ClickEvent.SuggestCommand("/todo remove %s".formatted(todo.id)))
        );
        return Component.literal(todo.success ? "☑" : "☐")
            .append(" ").append(component)
            .append(" ").append(todo.success ? unSuccess : success)
            .append(" ").append(remove);
    }


    public static class Todo {
        public final Long id;
        public final String desc;
        public boolean success;

        Todo(Long id, String desc, boolean success) {
            this.id = id;
            this.desc = desc;
            this.success = success;
        }
    }
}
