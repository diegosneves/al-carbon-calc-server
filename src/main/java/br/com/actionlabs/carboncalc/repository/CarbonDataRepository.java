package br.com.actionlabs.carboncalc.repository;

import br.com.actionlabs.carboncalc.model.CarbonEmissionStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarbonDataRepository extends MongoRepository<CarbonEmissionStats, String> {

}
