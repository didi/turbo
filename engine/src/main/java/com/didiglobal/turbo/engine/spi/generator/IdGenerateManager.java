package com.didiglobal.turbo.engine.spi.generator;

import com.didiglobal.turbo.engine.spi.TurboServiceLoader;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class IdGenerateManager {

     /**
      * id generator
      */
     private IdGenerator idGenerator;

     public IdGenerator getIdGenerator(){
          if(idGenerator != null){
               return idGenerator;
          }

          synchronized (IdGenerateManager.class){
               Collection<IdGenerator> serviceInterfaces = TurboServiceLoader.getServiceInterfaces(IdGenerator.class);
               // In the order in which the services are loaded, take the first implementation
               Optional<IdGenerator> optional = serviceInterfaces.stream().findFirst();
               if(optional.isPresent()){
                    idGenerator = optional.get();
                    return idGenerator;
               }
               return null;
          }
     }
}
