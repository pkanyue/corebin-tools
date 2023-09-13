package com.rlax.corebin.tools.callgraph.model;

import com.rlax.corebin.tools.callgraph.common.CallType;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 调用单元
 *
 * @author Rlax
 * @date 2023/09/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class CallItem {

    private CallClass callerClass;
    private CallMethod callerMethod;

    private CallClass calledClass;
    private CallMethod calledMethod;

    /**
     * 调用顺序号，调用顺序，从小到大
     */
    private Integer sortNum;

    /**
     * 方法内部，向下调用其他类方法的行数
     */
    private Integer callLineNum;

    private CallType callType;

}
