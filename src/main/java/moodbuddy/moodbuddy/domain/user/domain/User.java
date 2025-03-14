package moodbuddy.moodbuddy.domain.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.*;
import moodbuddy.moodbuddy.global.common.base.BaseTimeEntity;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_role", columnDefinition = "varchar(50)")
    private String userRole;

    @Column(name = "nickname", columnDefinition = "varchar(50)")
    private String nickname;

    @Column(name = "kakao_id", columnDefinition = "bigint")
    private Long kakaoId;

    @Column(name = "alarm", columnDefinition = "tinyint")
    private Boolean alarm;

    @Column(name = "alarm_time", columnDefinition = "varchar(10)")
    private String alarmTime;

    @Column(name = "letter_alarm", columnDefinition = "tinyint")
    private Boolean letterAlarm;

    @Column(name = "phone_number", columnDefinition = "varchar(20)")
    private String phoneNumber;

    @Column(name = "birthday", columnDefinition ="varchar(10)")
    private String birthday;

    @Column(name = "gender", columnDefinition = "tinyint")
    private Boolean gender;

    @Column(name = "deleted", columnDefinition = "tinyint")
    private Boolean deleted;

    @Column(name = "user_cur_diary_nums", columnDefinition = "int")
    private Integer userCurDiaryNums;

    @Column(name = "user_last_diary_nums", columnDefinition = "int")
    private Integer userLastDiaryNums;

    @Column(name = "user_letter_nums", columnDefinition = "int")
    private Integer userLetterNums;

    @Column(name = "check_today_diary")
    private Boolean checkTodayDairy;

    public void setCheckTodayDiary(Boolean checkTodayDairy) {
        this.checkTodayDairy = checkTodayDairy;
    }
    public void plusUserNumCount() {
        userCurDiaryNums++;
        userLetterNums++;
    }
    public void minusUserNumCount() {
        userCurDiaryNums--;
        userLetterNums--;
    }

    public void setUserLetterNums(int nums) {
        this.userLetterNums = nums;
    }
}
