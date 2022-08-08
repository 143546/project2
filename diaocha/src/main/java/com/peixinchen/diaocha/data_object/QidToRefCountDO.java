package com.peixinchen.diaocha.data_object;

import lombok.Data;

@Data
public class QidToRefCountDO {
    public Integer qid;
    public Integer refCount;

    public QidToRefCountDO(int qid, int refCount) {
        this.qid = qid;
        this.refCount = refCount;
    }
}
