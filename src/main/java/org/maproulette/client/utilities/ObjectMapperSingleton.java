package org.maproulette.client.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.maproulette.client.model.RuleList;

public class ObjectMapperSingleton
{
    private static volatile ObjectMapper mapper;

    public static ObjectMapper getMapper()
    {
        if (mapper == null)
        {
            synchronized (ObjectMapper.class)
            {
                if (mapper == null)
                {
                    mapper = new ObjectMapper();
                    SimpleModule module = new SimpleModule();
                    module.addSerializer(RuleList.class, new RuleList.RuleListSerializer());
                    module.addDeserializer(RuleList.class, new RuleList.RuleListDeserializer());
                    mapper.registerModule(module);
                }
            }
        }

        return mapper;
    }
}
