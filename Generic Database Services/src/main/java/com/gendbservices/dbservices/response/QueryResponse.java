package com.gendbservices.dbservices.response;

import com.google.common.base.MoreObjects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse {

    int recordCount = 0;
    protected List<List<Object>> records;
    public void addRecord(List<Object> record){

        this.records.add(record);
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this).add("records", records)
                .add("resultLimit", recordCount).toString();
    }
}
