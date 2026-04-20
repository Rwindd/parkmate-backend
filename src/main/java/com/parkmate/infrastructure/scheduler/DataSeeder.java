package com.parkmate.infrastructure.scheduler;

import com.parkmate.domain.port.*;
import com.parkmate.infrastructure.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Dev data seeder — only runs on profile=dev (not prod).
 * Pattern: Template Method / CommandLineRunner.
 * Seeds: 8 users, 6 events, 4 anon posts, 3 chat messages.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepositoryPort userRepo;
    private final EventRepositoryPort eventRepo;
    private final AnonPostRepositoryPort anonRepo;
    private final ChatMessageRepositoryPort chatRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepo.existsByDeviceId("seed-device-001")) {
            log.info("DataSeeder: already seeded, skipping.");
            return;
        }

        log.info("DataSeeder: seeding dev data...");

        // ── Users ──
        List<UserEntity> users = List.of(
            user("Ravi M.",     "HP",        "Citius",  "4",  "+91 98001 11111", "seed-device-001"),
            user("Priya K.",    "Verizon",   "Altius",  "9",  "+91 98001 22222", "seed-device-002"),
            user("Suresh A.",   "Cognizant", "Fortius", "2",  "+91 98001 33333", "seed-device-003"),
            user("Anand K.",    "HP",        "Citius",  "7",  "+91 98001 44444", "seed-device-004"),
            user("Deepa R.",    "Infosys",   "Altius",  "5",  "+91 98001 55555", "seed-device-005"),
            user("Siva P.",     "TCS",       "Fortius", "3",  "+91 98001 66666", "seed-device-006"),
            user("Kiran P.",    "DXC",       "Citius",  "6",  "+91 98001 77777", "seed-device-007"),
            user("Karthik M.",  "HP",        "Citius",  "4",  "+91 98001 88888", "seed-device-008")
        );
        List<UserEntity> saved = users.stream().map(userRepo::save).toList();
        UserEntity ravi    = saved.get(0);
        UserEntity priya   = saved.get(1);
        UserEntity suresh  = saved.get(2);
        UserEntity anand   = saved.get(3);
        UserEntity siva    = saved.get(5);

        // ── Events ──
        EventEntity cricket = EventEntity.builder()
            .module("sports").activity("Cricket").activityIcon("🏏")
            .title("Sunday morning cricket — HP vs Verizon 🏏")
            .eventDate(LocalDate.now().plusDays(3)).eventTime(LocalTime.of(7, 0))
            .location("Guindy Ground").spots(10).visibility("all")
            .creator(ravi).joiners(new ArrayList<>(List.of(priya, suresh)))
            .build();

        EventEntity badminton = EventEntity.builder()
            .module("sports").activity("Badminton").activityIcon("🏸")
            .title("Doubles match — need one more partner 🏸")
            .eventDate(LocalDate.now().plusDays(1)).eventTime(LocalTime.of(6, 30))
            .location("Indoor Court, Guindy").spots(4).visibility("all")
            .creator(priya).joiners(new ArrayList<>(List.of(anand, siva)))
            .build();

        EventEntity biryaniRun = EventEntity.builder()
            .module("lunch").activity("Biryani Run").activityIcon("🍛")
            .title("Biryani run to Buhari — 8 seats in cab 🍛")
            .eventDate(LocalDate.now()).eventTime(LocalTime.of(13, 0))
            .location("Buhari Hotel, Anna Salai").spots(8).visibility("all")
            .creator(suresh).joiners(new ArrayList<>(List.of(ravi, priya, anand)))
            .build();

        EventEntity bgmi = EventEntity.builder()
            .module("gaming").activity("BGMI").activityIcon("🎮")
            .title("BGMI squad tonight — need 2 more 🎮")
            .eventDate(LocalDate.now()).eventTime(LocalTime.of(21, 0))
            .location("Online — Erangel").spots(4).visibility("all")
            .creator(siva).joiners(new ArrayList<>(List.of(anand)))
            .build();

        EventEntity reactHack = EventEntity.builder()
            .module("build").activity("Hackathon").activityIcon("💻")
            .title("React hackathon weekend — build something cool 💻")
            .eventDate(LocalDate.now().plusDays(5)).eventTime(LocalTime.of(10, 0))
            .location("Citius Floor 7, Conference Room").spots(6).visibility("all")
            .creator(anand).joiners(new ArrayList<>(List.of(priya)))
            .build();

        // Expired event (for History page demo)
        EventEntity oldChess = EventEntity.builder()
            .module("sports").activity("Chess").activityIcon("♟️")
            .title("Chess tournament at food court ♟️")
            .eventDate(LocalDate.now().minusDays(2)).eventTime(LocalTime.of(13, 15))
            .location("Food Court, Altius").spots(4).visibility("all")
            .creator(ravi).joiners(new ArrayList<>(List.of(suresh, siva)))
            .active(true).expired(true)
            .build();

        List.of(cricket, badminton, biryaniRun, bgmi, reactHack, oldChess)
            .forEach(eventRepo::save);

        // ── Anon Posts ──
        List.of(
            "The food court AC is set to -40°C again. We're humans not penguins 🥶",
            "If your Teams call could've been an email, PLEASE make it an email. Three meetings = zero work done.",
            "Parking at B block is chaos at 9am. Security just watches. Someone escalate! 🚗",
            "Appraisal season and everyone's suddenly working late… coincidence? 😂"
        ).forEach(txt -> {
            AnonPostEntity post = AnonPostEntity.builder().text(txt).build();
            anonRepo.save(post);
        });

        // ── Chat Messages ──
        ChatMessageEntity sys = ChatMessageEntity.builder()
            .systemMessage(true).text("Clockpoint chat is open. Say hi to your fellow Olympians 👋")
            .build();
        ChatMessageEntity c1 = ChatMessageEntity.builder()
            .sender(ravi).text("Anyone up for chai? I'm at the clockpoint right now ☕").build();
        ChatMessageEntity c2 = ChatMessageEntity.builder()
            .sender(priya).text("Coming! 5 mins 🏃").build();
        List.of(sys, c1, c2).forEach(chatRepo::save);

        // Mark Ravi as at clockpoint
        ravi.setAtClockpoint(true);
        userRepo.save(ravi);

        log.info("DataSeeder: ✅ seeded {} users, 6 events, 4 anon posts, 3 chat messages", saved.size());
    }

    private UserEntity user(String name, String co, String tw, String fl, String phone, String did) {
        return UserEntity.builder()
            .name(name).company(co).tower(tw).floor(fl).phone(phone).deviceId(did).build();
    }
}
