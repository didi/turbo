package com.didiglobal.turbo.demo.service;

import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.hook.HookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HookServiceImpl implements HookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HookServiceImpl.class);

    @Override
    public List<InstanceData> invoke(String flowInstanceId, String nodeInstanceId, String nodeKey, String hookInfoParam) {
        LOGGER.info("flowInstance={}||nodeInstanceId={}||nodeKey={}||hookInfoParam={}",
            flowInstanceId, nodeInstanceId, nodeKey, hookInfoParam);
        // do sth , eg: refresh info
        return new ArrayList<>();
    }
}
