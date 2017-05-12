package com.weatherrisk.api.cnst.movie;

import com.weatherrisk.api.cnst.movie.theaters.AmbassadorTheater;
import com.weatherrisk.api.cnst.movie.theaters.MiramarTheater;
import com.weatherrisk.api.cnst.movie.theaters.ShowTimeTheater;
import com.weatherrisk.api.cnst.movie.theaters.ViewshowTheater;

public enum SupprotedTheaterCompany {
	AMBASSADOR("國賓影城", AmbassadorTheater.values()),
	MIRAMAR("美麗華影城", MiramarTheater.values()),
	SHOWTIME("秀泰影城", ShowTimeTheater.values()),
	VIEWSHOW("威秀影城", ViewshowTheater.values())
	;
	
	private String theaterCompanyName;
	private MovieTheater[] movieTheaters;
	
	private SupprotedTheaterCompany(String theaterCompanyName, MovieTheater[] movieTheaters) {
		this.theaterCompanyName = theaterCompanyName;
		this.movieTheaters = movieTheaters;
	}

	public String getTheaterCompanyName() {
		return theaterCompanyName;
	}

	public MovieTheater[] getMovieTheaters() {
		return movieTheaters;
	}
	
	public static SupprotedTheaterCompany convertByCompanyName(String companyName) {
		for (SupprotedTheaterCompany e : SupprotedTheaterCompany.values()) {
			if (e.getTheaterCompanyName().equals(companyName)) {
				return e;
			}
		}
		return null;
	}
	
	public static SupprotedTheaterCompany convertByName(String name) {
		for (SupprotedTheaterCompany e : SupprotedTheaterCompany.values()) {
			if (e.toString().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
