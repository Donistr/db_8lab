package org.example.db_8lab;

import org.example.db_8lab.entity.Comment;
import org.example.db_8lab.entity.GroupMember;
import org.example.db_8lab.entity.Role;
import org.example.db_8lab.entity.User;
import org.example.db_8lab.entity.UserGroup;
import org.example.db_8lab.repository.CommentRepository;
import org.example.db_8lab.repository.GroupMemberRepository;
import org.example.db_8lab.repository.JdbcTemplateRepository;
import org.example.db_8lab.repository.PostRepository;
import org.example.db_8lab.repository.UserGroupRepository;
import org.example.db_8lab.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Db8labApplication {

    private static JdbcTemplateRepository jdbcTemplateRepository;

    private static CommentRepository commentRepository;

    private static GroupMemberRepository groupMemberRepository;

    private static PostRepository postRepository;

    private static UserGroupRepository userGroupRepository;

    private static UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Db8labApplication.class, args);

        setup();

        createAndUpdateUser();

        jdbcTemplateRepository.getAllUsersInGroup("Группа любителей кино");
        getAllUsersInGroup("Группа любителей кино");

        jdbcTemplateRepository.getPostCountForEachGroup();
        getPostCountForEachGroup();

        jdbcTemplateRepository.getUserCountForEachRoleForGroupCreatedInYear(2024);
        getUserCountForEachRoleForGroupCreatedInYear(2024);

        jdbcTemplateRepository.getInfoAboutPostsWhichCommentedMoreThanTimes(0);
        getInfoAboutPostsWhichCommentedMoreThanTimes(0);

        jdbcTemplateRepository.getGroupsWithMaxMembersCount();
        getGroupsWithMaxMembersCount();

        jdbcTemplateRepository.getPostCountGroupsStatistic();
        getPostCountGroupsStatistic();
    }

    private static void setup() {
        jdbcTemplateRepository = ApplicationContextProvider.getApplicationContext().getBean(JdbcTemplateRepository.class);

        commentRepository = ApplicationContextProvider.getApplicationContext().getBean(CommentRepository.class);
        groupMemberRepository = ApplicationContextProvider.getApplicationContext().getBean(GroupMemberRepository.class);
        postRepository = ApplicationContextProvider.getApplicationContext().getBean(PostRepository.class);
        userGroupRepository = ApplicationContextProvider.getApplicationContext().getBean(UserGroupRepository.class);
        userRepository = ApplicationContextProvider.getApplicationContext().getBean(UserRepository.class);
    }

    /**
     * С использованием технологии ORM выполняется модификация данных в таблицах.
     * */
    private static void createAndUpdateUser() {
        User user = new User();
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setEmail("ivan@gmail.com");
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        user.setEmail("changed@gmail.com");
        userRepository.save(user);
    }

    /**
     * Запрос 1.
     * Вывести всех пользователей, состоящих в группе "Группа любителей кино", с указанием их роли. В запросе вывести идентификатор группы, название группы, имя пользователя, фамилию, название роли.
     * */
    private static void getAllUsersInGroup(String groupName) {
        UserGroup userGroup = userGroupRepository.findAllByName(groupName).get(0);
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(userGroup);

        groupMembers.forEach(groupMember -> {
            User user = groupMember.getUser();
            System.out.println(userGroup.getGroupId() + " | " + userGroup.getName() + " | " +
                    user.getFirstName() + " | " + user.getLastName() + " | " + groupMember.getRole().getRoleName());
        });
    }

    /**
     * Запрос 2.
     * Вывести информацию о количестве постов в каждой группе. Если постов не было - вывести 0. Упорядочить по количеству постов от большего к меньшему. В запросе вывести название группы, описание, количество постов (столбец назвать count).
     */
    private static void getPostCountForEachGroup() {
        Map<UserGroup, Integer> userGroupsPostCount = new HashMap<>();
        userGroupRepository.findAll().forEach(userGroup ->
                userGroupsPostCount.put(userGroup, postRepository.findAllByGroup(userGroup).size()));

        userGroupsPostCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    UserGroup group = entry.getKey();
                    System.out.println(group.getName() + " | " + group.getDescription() + " | " + entry.getValue());
                });
    }

    /**
     * Запрос 3.
     * Для групп, созданных в 2023 году, вывести количество пользователей каждой роли. Если пользователи с какой-то ролью в группе отсутствуют - роль не выводить. В запросе вывести столбцы: название группы, дата создания, название роли, количество пользователей с этой ролью (столбец назвать count).
     * */
    private static void getUserCountForEachRoleForGroupCreatedInYear(int year) {
        userGroupRepository.findAll().stream()
                .filter(userGroup -> userGroup.getCreatedAt().getYear() == year)
                .forEach(userGroup -> {
                    List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(userGroup);

                    Map<Role, Integer> roleCounts = new HashMap<>();
                    groupMembers.forEach(groupMember -> {
                        Role role = groupMember.getRole();
                        if (!roleCounts.containsKey(role)) {
                            roleCounts.put(role, 0);
                        }

                        roleCounts.put(role, roleCounts.get(role) + 1);
                    });

                    roleCounts.forEach((role, count) -> System.out.println(userGroup.getName() + " | "
                            + userGroup.getCreatedAt() + " | " + role.getRoleName() + " | " + count));
                });
    }

    /**
     * Запрос 4.
     * Вывести информацию о постах, которые прокомментировали больше 2 раз. В запросе вывести столбцы: название группы, текст поста, имя автора, фамилия автора, количество комментариев (столбец назвать count).
     * */
    private static void getInfoAboutPostsWhichCommentedMoreThanTimes(int times) {
        postRepository.findAll().stream()
                .forEach(post -> {
                    List<Comment> comments = commentRepository.findAllByPost(post);
                    if (comments.size() > times) {
                        User user = post.getUser();
                        System.out.println(post.getGroup().getName() + " | " + post.getContent() + " | " +
                                user.getFirstName() + " | " + user.getLastName() + " | " + comments.size());
                    }
                });
    }

    /**
     * Запрос 5.
     * Вывести список всех групп, в которых состоит максимальное число участников. В запросе вывести название группы, имя владельца группы, фамилию владельца группы.
     */
    private static void getGroupsWithMaxMembersCount() {
        Map<Integer, List<UserGroup>> membersCountForGroups = new HashMap<>();
        userGroupRepository.findAll().forEach(userGroup -> {
            int membersCount = groupMemberRepository.findAllByGroup(userGroup).size();
            List<UserGroup> groups = membersCountForGroups.getOrDefault(membersCount, new ArrayList<>());
            groups.add(userGroup);
            membersCountForGroups.put(membersCount, groups);
        });

        membersCountForGroups.entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .orElseThrow()
                .getValue()
                .forEach(group -> {
                    User owner = group.getOwner();
                    System.out.println(group.getName() + " | " + owner.getFirstName() + " | " + owner.getLastName());
                });
    }

    /**
     * Запрос 6.
     * Посчитать статистику количества постов в группах. В запросе вывести минимальное (столбец назвать min), среднее (avg) и максимальное (max) количество постов.
     */
    private static void getPostCountGroupsStatistic() {
        Integer min = null;
        Integer max = null;
        int postsCount = 0;
        int groupsCount = 0;

        List<UserGroup> groups = userGroupRepository.findAll();
        for (UserGroup group : groups) {
            int postCount = postRepository.findAllByGroup(group).size();

            if (min == null || postCount < min) {
                min = postCount;
            }

            if (max == null || postCount > max) {
                max = postCount;
            }

            postsCount += postCount;
            ++groupsCount;
        }

        System.out.println(min + " | " + max + " | " + (double) postsCount / groupsCount);
    }

}
