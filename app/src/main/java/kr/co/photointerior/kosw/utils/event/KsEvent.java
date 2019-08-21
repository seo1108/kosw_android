package kr.co.photointerior.kosw.utils.event;

import java.io.Serializable;

/**
 * event bus event class.
 */
public class KsEvent<T extends Object> implements Serializable{
    public enum Type{
        /** 메인화면 회사 색상 적용 */
        CHANGE_COLOR,
        /** 각 캐릭터를 보유한 화면 캐릭터 변경 */
        CHANGE_CHARACTER,
        /** 계단을 올라간 층 수 메인화면 반영 */
        UPDATE_FLOOR_AMOUNT,
        /** 계단을 올라간 데이터 전송 실패시 */
        UPDATE_FLOOR_AMOUNT_FAIL,
        /** 메인화면 갱신을 위한 푸시 */
        MAIN_PUSH,
        /** 메인화면 갱신 */
        MAIN_REFRESH,
        /** 공지사항 이벤트 */
        NOTICE_EVENT

    }
    /** 이벤트가 전파될 때 Type */
    private Type type;
    /** 이 클래스가 보유가게 될 데이터 */
    private  T value;
    private boolean mainCharacterChanged = false;
    public Type getType() {
        return type;
    }

    public KsEvent setType(Type type) {
        this.type = type;
        return this;
    }

    public T getValue(){
        return value;
    }

    public KsEvent setValue( T value ){
        this.value = value;
        return this;
    }

    public boolean isMainCharacterChanged() {
        return mainCharacterChanged;
    }

    public KsEvent setMainCharacterChanged(boolean mainCharacterChanged) {
        this.mainCharacterChanged = mainCharacterChanged;
        return this;
    }
}
