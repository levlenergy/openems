package io.openems.edge.levl.simulator.datasource.csv.predefined;

public enum Source {
	ZERO("zero.csv"), //
	H0_HOUSEHOLD_SUMMER_WEEKDAY_STANDARD_LOAD_PROFILE("h0-summer-weekday-standard-load-profile.csv"), //
	H0_HOUSEHOLD_SUMMER_WEEKDAY_PV_PRODUCTION("h0-summer-weekday-pv-production.csv"), //
	H0_HOUSEHOLD_SUMMER_WEEKDAY_NON_REGULATED_CONSUMPTION("h0-summer-weekday-non-regulated-consumption.csv"), //
	H0_HOUSEHOLD_SUMMER_WEEKDAY_PV_PRODUCTION2("h0-summer-weekday-pv-production2.csv"),

	ONE_WEEK_CONSUMPTION_PEAK_SHAVING("one-week-consumption-peak-shaving.csv"),
	FEMS_KW36_CONSUMPTION("fems-kw36-consumption.csv"),
	FEMS_KW36_PRODUCTION("fems-kw36-production.csv"),
	FEMS_KW38_CONSUMPTION("fems-kw38-consumption.csv"),
	FEMS_KW38_PRODUCTION("fems-kw38-production.csv"),
	FEMS_KW41_CONSUMPTION("fems-kw41-consumption.csv"),
	FEMS_KW41_PRODUCTION("fems-kw41-production.csv"),
	FEMS_KW42_CONSUMPTION("fems-kw42-consumption.csv"),
	FEMS_KW42_PRODUCTION("fems-kw42-production.csv"),
	FEMS_KW43_CONSUMPTION("fems-kw43-consumption.csv"),
	FEMS_KW43_PRODUCTION("fems-kw43-production.csv")
	;

	public final String filename;

	private Source(String filename) {
		this.filename = filename;
	}
}
