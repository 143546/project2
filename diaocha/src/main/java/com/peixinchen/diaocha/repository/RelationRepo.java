package com.peixinchen.diaocha.repository;

import com.peixinchen.diaocha.data_object.QidToRefCountDO;
import com.peixinchen.diaocha.util.DBUtil;
import com.peixinchen.diaocha.util.Log;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RelationRepo {
    @SneakyThrows
    public List<QidToRefCountDO> selectCountGroupByQid(List<Integer> qidList) {
        String sqlFormat = "select qid, count(*) as ref_count from relations where qid in (%s) group by qid order by qid";
        StringBuilder sb = new StringBuilder();
        for (Integer qid : qidList) {
            sb.append(qid).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());    // 删除最后多余的 ", "
        String sql = String.format(sqlFormat, sb.toString());

        List<QidToRefCountDO> list = new ArrayList<>();
        try (Connection c = DBUtil.connection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                Log.println("执行 SQL: " + ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        QidToRefCountDO qidToRefCountDO = new QidToRefCountDO(
                                rs.getInt("qid"),
                                rs.getInt("ref_count")
                        );
                        list.add(qidToRefCountDO);
                    }
                }
            }
        }
        return list;
    }

    @SneakyThrows
    public List<Integer> selectQidListBySid(int sid) {
        String sql = "select qid from relations where sid = ? order by rid";
        List<Integer> qidList = new ArrayList<>();
        try (Connection c = DBUtil.connection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, sid);

                Log.println("执行 SQL: " + ps);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        qidList.add(rs.getInt("qid"));
                    }
                }
            }
        }

        return qidList;
    }

    public static void main(String[] args) {
        List<Integer> qidList = Arrays.asList(1, 2, 4, 5);
        RelationRepo relationRepo = new RelationRepo();
        List<QidToRefCountDO> list = relationRepo.selectCountGroupByQid(qidList);
        System.out.println(list);
    }

    @SneakyThrows
    public void deleteBySidAndQidList(int sid, List<Integer> qidList) {
        String sqlFormat = "delete from relations where sid = ? and qid in (%s)";
        String join = String.join(", ", qidList.stream().map(String::valueOf).collect(Collectors.toList()));
        String sql = String.format(sqlFormat, join);

        try (Connection c = DBUtil.connection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, sid);

                Log.println("执行 SQL: " + ps);

                ps.executeUpdate();
            }
        }
    }

    @SneakyThrows
    public void insertBySidAndQidList(int sid, List<Integer> bindQidList) {
        List<String> collect = bindQidList.stream().map(qid -> String.format("(%d, %d)", sid, qid)).collect(Collectors.toList());
        Log.println(collect);
        String sql = "insert into relations (sid, qid) values " + String.join(", ", collect);

        try (Connection c = DBUtil.connection()) {
            try (PreparedStatement ps = c.prepareStatement(sql)) {

                Log.println("执行 SQL: " + ps);

                ps.executeUpdate();
            }
        }
    }
}
