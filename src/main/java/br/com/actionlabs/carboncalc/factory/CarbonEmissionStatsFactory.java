package br.com.actionlabs.carboncalc.factory;

import br.com.actionlabs.carboncalc.model.CarbonEmissionStats;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.utils.IdentifierUtil;

public class CarbonEmissionStatsFactory {

    private CarbonEmissionStatsFactory() {}

    public static CarbonEmissionStats create(final UserData userData) {
        return new CarbonEmissionStats(IdentifierUtil.unique(), userData);
    }

}
