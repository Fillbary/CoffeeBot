package com.example.CoffeeBot.Service;

import com.example.CoffeeBot.Entity.CoffeeMeeting;
import com.example.CoffeeBot.Entity.Subscriber;
import com.example.CoffeeBot.Repository.CoffeeMeetingRepository;
import com.example.CoffeeBot.Repository.SubscriberRepository;
import com.example.CoffeeBot.Utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@Transactional
public class PairMatchingService {
    private final SubscriberRepository subscriberRepository;
    private final CoffeeMeetingRepository coffeeMeetingRepository;
    private final DateUtils dateUtils;

    private static final int PREVIOUS_WEEKS_TO_CHECK = 1;

    public PairMatchingService(SubscriberRepository subscriberRepository,
                               CoffeeMeetingRepository coffeeMeetingRepository,
                               DateUtils dateUtils) {
        this.subscriberRepository = subscriberRepository;
        this.coffeeMeetingRepository = coffeeMeetingRepository;
        this.dateUtils = dateUtils;
    }

    /**
     * Основной метод для еженедельного формирования пар
     * Выполняет полный цикл: получение активных пользователей, проверка истории, создание и сохранение встреч
     *
     * @return список созданных и сохраненных встреч CoffeeMeeting
     */
    @Transactional
    public List<CoffeeMeeting> generateWeeklyPairs() {
        List<Subscriber> activeSubscribers = subscriberRepository.findByIsActive();
        Map<Long, Set<Long>> recentMeetings = getRecentMeetingsMap();

        List<CoffeeMeeting> meetings = generatePairs(activeSubscribers, recentMeetings);

        return coffeeMeetingRepository.saveAll(meetings);
    }

    /**
     * Генерирует список встреч на основе активных подписчиков и истории предыдущих встреч
     *
     * @param activeSubscribers список активных подписчиков для распределения по парам
     * @param recentMeetings    карта истории встреч для избежания повторных встреч
     * @return список созданных встреч (CoffeeMeeting)
     */
    public List<CoffeeMeeting> generatePairs(List<Subscriber> activeSubscribers, Map<Long, Set<Long>> recentMeetings) {
        List<Subscriber> shuffledSubscribers = shuffleSubscribers(activeSubscribers);
        LocalDate nextWeek = dateUtils.getNextWeekMonday();

        List<CoffeeMeeting> newMeetings = createBasicPairs(shuffledSubscribers, recentMeetings);

        if (shuffledSubscribers.size() % 2 != 0) {
            handleOddSubscriber(shuffledSubscribers.getLast(), newMeetings, nextWeek);
        }

        return newMeetings;
    }

    /**
     * Создает базовые пары из перемешанного списка подписчиков с учетом истории встреч
     *
     * @param shuffledUsers  перемешанный список подписчиков
     * @param recentMeetings карта истории встреч для проверки повторных встреч
     * @return список созданных кофейных встреч
     */
    private List<CoffeeMeeting> createBasicPairs(List<Subscriber> shuffledUsers, Map<Long, Set<Long>> recentMeetings) {
        List<CoffeeMeeting> coffeeMeetings = new ArrayList<>();
        LocalDate nextWeek = dateUtils.getNextWeekMonday();

        List<Subscriber> availableUsers = new ArrayList<>(shuffledUsers);

        for (int i = 0; i < shuffledUsers.size(); i += 2) {
            if (i + 1 < shuffledUsers.size()) {
                // Создаем пару из users[i] и users[i+1]
                Subscriber subscriber1 = shuffledUsers.get(i);
                Subscriber subscriber2 = findBestPartner(subscriber1, availableUsers, i + 1, recentMeetings);

                CoffeeMeeting meeting = new CoffeeMeeting(nextWeek, subscriber1, subscriber2);
                coffeeMeetings.add(meeting);
                log.info("Pair created: {} - {}", subscriber1.getUserName(), subscriber2.getUserName());
            }
        }

        return coffeeMeetings;
    }

    /**
     * Находит наилучшего партнера для заданного подписчика из списка доступных кандидатов.
     * Приоритет отдается подписчикам, которые еще не встречались с исходным подписчиком.
     * Если все кандидаты уже встречались - возвращает первого доступного партнера.
     *
     * @param subscriber    подписчик, для которого ищется партнер
     * @param availableUsers список доступных для выбора подписчиков
     * @param startIndex     индекс в списке availableUsers, с которого начинать поиск кандидатов
     * @param recentMeetings карта истории встреч для проверки повторных встреч
     * @return наилучший доступный партнер для создания встречи
     */
    private Subscriber findBestPartner(Subscriber subscriber, List<Subscriber> availableUsers,
                                       int startIndex, Map<Long, Set<Long>> recentMeetings) {
        // Сначала ищем тех, кто не встречался
        for (int i = startIndex; i < availableUsers.size(); i++) {
            Subscriber candidate = availableUsers.get(i);
            if (!haveSubscribersMetRecently(subscriber, candidate, recentMeetings)) {
                return candidate;
            }
        }
        // Если все встречались - возвращаем первого доступного
        return availableUsers.get(startIndex);
    }

    /**
     * Проверяет, встречались ли два пользователя недавно (за последние N недель)
     * Использует предварительно построенную карту встреч для быстрой проверки
     *
     * @param subscriber1    - первый пользователь
     * @param subscriber2    - второй пользователь
     * @param recentMeetings - карта недавних встреч
     * @return boolean - true если пользователи встречались, false если нет
     */
    private boolean haveSubscribersMetRecently(
            Subscriber subscriber1,
            Subscriber subscriber2,
            Map<Long, Set<Long>> recentMeetings) {

        Long subscriber1Id = subscriber1.getId();
        Long subscriber2Id = subscriber2.getId();
        Set<Long> subscriber1Meetings = recentMeetings.get(subscriber1Id);
        if (subscriber1Meetings == null) {
            return false;
        }
        boolean haveMet = subscriber1Meetings.contains(subscriber2Id);
        if (haveMet) {
            log.debug("Users {} and {} have met recently", subscriber1.getUserName(), subscriber2.getUserName());
        }
        return haveMet;
    }

    /**
     * Обрабатывает случай нечетного количества подписчиков, добавляя последнего в существующую встречу
     *
     * @param oddSubscriber оставшийся подписчик без пары
     * @param meetings      список уже созданных встреч
     * @param nextWeek      дата следующей недели для встреч
     */
    private void handleOddSubscriber(Subscriber oddSubscriber, List<CoffeeMeeting> meetings, LocalDate nextWeek) {
        // Проверяем что есть встречи на следущей неделе
        if (meetings != null && !meetings.isEmpty()) {
            // Берем последнюю встречу из уже составленных
            CoffeeMeeting lastMeeting = meetings.get(meetings.size() - 1);
            // Добавляем одинокого подписчика к последней встречи
            lastMeeting.setSubscriber3(oddSubscriber);
            log.info("A subscriber {} has been added to the meeting {}", oddSubscriber.getUserName(), lastMeeting.getId());
        } else {
            CoffeeMeeting lonelyMeeting = new CoffeeMeeting(nextWeek, oddSubscriber, null);
            meetings.add(lonelyMeeting);
            log.warn("Only one active subscriber: {}", oddSubscriber.getUserName());
        }
    }

    /**
     * Перемешивает список подписчиков в случайном порядке для обеспечения случайности распределения
     *
     * @param subscribers список подписчиков для перемешивания
     * @return перемешанный список подписчиков
     */
    private List<Subscriber> shuffleSubscribers(List<Subscriber> subscribers) {
        if (subscribers == null) {
            log.warn("Passed null instead of a list of users, returning an empty list");
            return Collections.emptyList();
        }

        List<Subscriber> shuffledSubscribers = new ArrayList<>(subscribers);
        Collections.shuffle(shuffledSubscribers);
        return shuffledSubscribers;
    }

    /**
     * Создает карту истории встреч для быстрой проверки, кто с кем встречался
     * Структура карты: Map<subscriberId, Set<subscriberId>> - для каждого пользователя хранится
     * множество ID пользователей, с которыми он встречался за последние N недель
     * Пример карты:
     * {
     * 101: [102, 103],  // пользователь 101 встречался с 102 и 103
     * 102: [101],       // пользователь 102 встречался с 101
     * 103: [101]        // пользователь 103 встречался с 101
     * }
     *
     * @return Map<Long, Set < Long>> - карта встреч пользователей
     */
    public Map<Long, Set<Long>> getRecentMeetingsMap() {
        // Получаем дату, начиная с которой будем искать встречи (понедельник N недель назад)
        LocalDate startDate = dateUtils.getPreviousWeek(PREVIOUS_WEEKS_TO_CHECK);
        // Получаем все встречи за указанный период из базы данных
        List<CoffeeMeeting> recentMeetings = coffeeMeetingRepository.findByWeekStartDate(startDate);
        // Инициализируем карту для хранения отношений встреч
        Map<Long, Set<Long>> meetingsMap = new HashMap<>();
        // Обрабатываем каждую встречу и добавляем в карту
        for (CoffeeMeeting meeting : recentMeetings) {
            processMeetingForMap(meetingsMap, meeting);
        }
        return meetingsMap;
    }

    /**
     * Обрабатывает одну встречу и добавляет информацию о ней в карту встреч
     * Учитывает как пары (2 пользователя), так и тройки (3 пользователя)
     *
     * @param meetingsMap - карта для добавления встреч
     * @param meeting     - обрабатываемая встреча
     */
    private void processMeetingForMap(Map<Long, Set<Long>> meetingsMap, CoffeeMeeting meeting) {
        // Для пар: добавляем связь между user1 и user2
        addMeetingPairToMap(meetingsMap, meeting.getSubscriber1().getId(), meeting.getSubscriber2().getId());
        // Для троек: добавляем связи между всеми тремя пользователями
        if (meeting.getSubscriber3() != null) {
            addMeetingPairToMap(meetingsMap, meeting.getSubscriber1().getId(), meeting.getSubscriber3().getId());
            addMeetingPairToMap(meetingsMap, meeting.getSubscriber2().getId(), meeting.getSubscriber3().getId());
        }
    }

    /**
     * Вспомогательный метод для добавления пары пользователей в карту встреч
     * Добавляет двустороннюю связь: user1->user2 и user2->user1
     *
     * @param meetingsMap   - карта встреч
     * @param subscriber1Id - ID первого пользователя
     * @param subscriber2Id - ID второго пользователя
     */
    private void addMeetingPairToMap(Map<Long, Set<Long>> meetingsMap, Long subscriber1Id, Long subscriber2Id) {
        // computeIfAbsent — это метод в Java 8+, который используется для работы с коллекциями (Map),
        // позволяя вычислять и добавлять значение по ключу только в том случае, если такой ключ еще не существует.
        meetingsMap.computeIfAbsent(subscriber1Id, key -> new HashSet<>()).add(subscriber2Id);
        meetingsMap.computeIfAbsent(subscriber2Id, key -> new HashSet<>()).add(subscriber1Id);
    }
}
