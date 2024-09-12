package org.example.db_8lab.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Запрос 1. Вывести всех пользователей, состоящих в группе "Группа любителей кино", с указанием их роли. В запросе вывести идентификатор группы, название группы, имя пользователя, фамилию, название роли.
     */
    public void getAllUsersInGroup(String groupName) {
        String sql = "SELECT ug.group_id, ug.name, u.first_name, u.last_name, r.role_name " +
                "FROM user_groups ug " +
                "JOIN group_members gm ON ug.group_id = gm.group_id " +
                "JOIN users u ON gm.user_id = u.user_id " +
                "JOIN roles r ON gm.role_id = r.role_id " +
                "WHERE ug.name = ?";

        jdbcTemplate.queryForList(sql, groupName).forEach(System.out::println);
    }

    /**
     * Запрос 2. Вывести информацию о количестве постов в каждой группе. Если постов не было - вывести 0. Упорядочить по количеству постов от большего к меньшему. В запросе вывести название группы, описание, количество постов (столбец назвать count).
     */
    public void getPostCountForEachGroup() {
        String sql = "SELECT ug.name, ug.description, COUNT(p.post_id) AS count " +
                "FROM user_groups ug " +
                "LEFT JOIN posts p ON ug.group_id = p.group_id " +
                "GROUP BY ug.group_id, ug.name, ug.description " +
                "ORDER BY count DESC";

        jdbcTemplate.queryForList(sql).forEach(System.out::println);
    }

    /**
     * Запрос 3. Для групп, созданных в 2023 году, вывести количество пользователей каждой роли. Если пользователи с какой-то ролью в группе отсутствуют - роль не выводить. В запросе вывести столбцы: название группы, дата создания, название роли, количество пользователей с этой ролью (столбец назвать count).
     */
    public void getUserCountForEachRoleForGroupCreatedInYear(int year) {
        String sql = "SELECT ug.name, ug.created_at, r.role_name, COUNT(gm.user_id) AS count " +
                "FROM user_groups ug " +
                "JOIN group_members gm ON ug.group_id = gm.group_id " +
                "JOIN roles r ON gm.role_id = r.role_id " +
                "WHERE YEAR(ug.created_at) = ? " +
                "GROUP BY ug.group_id, ug.name, ug.created_at, r.role_name";

        jdbcTemplate.queryForList(sql, year).forEach(System.out::println);
    }

    /**
     * Запрос 4. Вывести информацию о постах, которые прокомментировали больше 2 раз. В запросе вывести столбцы: название группы, текст поста, имя автора, фамилия автора, количество комментариев (столбец назвать count).
     */
    public void getInfoAboutPostsWhichCommentedMoreThanTimes(int times) {
        String sql = "SELECT ug.name, p.content, u.first_name, u.last_name, COUNT(c.comment_id) AS count " +
                "FROM posts p " +
                "JOIN user_groups ug ON p.group_id = ug.group_id " +
                "JOIN users u ON p.user_id = u.user_id " +
                "LEFT JOIN comments c ON p.post_id = c.post_id " +
                "GROUP BY ug.name, p.content, u.first_name, u.last_name " +
                "HAVING COUNT(c.comment_id) > ?";

        jdbcTemplate.queryForList(sql, times).forEach(System.out::println);
    }

    /**
     * Запрос 5. Вывести список всех групп, в которых состоит максимальное число участников. В запросе вывести название группы, имя владельца группы, фамилию владельца группы.
     */
    public void getGroupsWithMaxMembersCount() {
        String sql = "WITH GroupMemberCounts AS (" +
                "SELECT ug.group_id, ug.name, ug.owner_id, COUNT(gm.user_id) AS member_count " +
                "FROM user_groups ug " +
                "JOIN group_members gm ON ug.group_id = gm.group_id " +
                "GROUP BY ug.group_id" +
                "), " +
                "MaxMemberCount AS (" +
                "SELECT MAX(member_count) AS max_members FROM GroupMemberCounts" +
                ") " +
                "SELECT gmc.name, u.first_name, u.last_name " +
                "FROM GroupMemberCounts gmc " +
                "JOIN MaxMemberCount mmc ON gmc.member_count = mmc.max_members " +
                "JOIN users u ON gmc.owner_id = u.user_id";

        jdbcTemplate.queryForList(sql).forEach(System.out::println);
    }

    /**
     * Запрос 6. Посчитать статистику количества постов в группах. В запросе вывести минимальное (столбец назвать min), среднее (avg) и максимальное (max) количество постов.
     */
    public void getPostCountGroupsStatistic() {
        String sql = "WITH GroupPostCounts AS (" +
                "SELECT ug.group_id, COUNT(p.post_id) AS post_count " +
                "FROM user_groups ug " +
                "LEFT JOIN posts p ON ug.group_id = p.group_id " +
                "GROUP BY ug.group_id" +
                ") " +
                "SELECT MIN(gpc.post_count) AS min, AVG(gpc.post_count) AS avg, MAX(gpc.post_count) AS max " +
                "FROM GroupPostCounts gpc";

        jdbcTemplate.queryForList(sql).forEach(System.out::println);
    }

}
