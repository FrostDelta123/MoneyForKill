package ru.frostdelta.spongepowered;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class EventListener {

    private Loader plugin;

    public EventListener(Loader instance){

        plugin = instance;

    }


    @Listener
    public void onEntityDeath(DestructEntityEvent.Death event) {


        Optional<EconomyService> serviceOpt = Sponge.getServiceManager().provide(EconomyService.class);
        EconomyService service = serviceOpt.get();
        if (!serviceOpt.isPresent()) {

        }
        EconomyService economyService = serviceOpt.get();

        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);

        if (optDamageSource.isPresent()) {
            EntityDamageSource damageSource = optDamageSource.get();
            Entity killer = damageSource.getSource();
            Entity victum = event.getTargetEntity();
            if(victum instanceof Player && killer instanceof Player && !((Player) victum).hasPermission("mfk.bypass")){
                Optional<UniqueAccount> account = economyService.getOrCreateAccount(victum.getUniqueId());
                //String groupVictum = String.valueOf(((Player) victum).getParents().get(0).getSubjectIdentifier());

                int withdraw = Integer.parseInt(((Player) victum).getOption("lose").get());
                BigDecimal requiredAmount = BigDecimal.valueOf(withdraw);

                TransactionResult result = account.get().withdraw(service.getDefaultCurrency(),
                        requiredAmount, Cause.of(event.getContext(), this));
                if (result.getResult() == ResultType.SUCCESS) {
                    ((Player) victum).sendMessage(ChatTypes.CHAT, Text.builder("Вы убиты! Вы потеряли: " + requiredAmount).color(TextColors.RED).build());
                } else if (result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS) {

                }
                String groupKiller = String.valueOf(((Player) killer).getParents().get(0).getSubjectIdentifier());
                int reward = Integer.parseInt(((Player) killer).getOption("reward").get());
                BigDecimal rewardAmount = BigDecimal.valueOf(reward);
                Optional<UniqueAccount> accountKiller = economyService.getOrCreateAccount(killer.getUniqueId());
                accountKiller.get().deposit(service.getDefaultCurrency(), rewardAmount, Cause.of(event.getContext(), this));
                ((Player) killer).sendMessage(ChatTypes.CHAT, Text.builder("Вы убили игрока! Ваша награда: " + rewardAmount).color(TextColors.GREEN).build());
            }
        }
    }

    @Listener
    public void reload(GameReloadEvent event) {

        Sponge.getEventManager().registerListeners(plugin, this);

    }



}
