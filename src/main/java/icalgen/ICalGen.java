package icalgen;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
// https://github.com/ical4j/ical4j/wiki/Examples

public class ICalGen {
	
	public static void generateICalTamilHolidays(String year) {
        Document doc;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // need http protocol
            doc = Jsoup.connect("https://www.officeholidays.com/countries/india/tamil_nadu/"+year+".php").get();
            ArrayList<String> downServers = new ArrayList<>();
            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");
            Calendar calendar = new Calendar();
            calendar.getProperties().add(new ProdId("-//Tamil Nadu Holidays//ThiruTricks 1.0//EN"));
            calendar.getProperties().add(Version.VERSION_2_0);
            calendar.getProperties().add(CalScale.GREGORIAN);

            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                String eventDate = row.select("td").get(1).select("time").text();
                String holidayname = row.select("td").get(2).text();
                String holidaydes = row.select("td").get(3).text();
                
                Date   date       = format.parse ( eventDate );

                // initialise as an all-day event..
                VEvent slgevnt = new VEvent(new net.fortuna.ical4j.model.Date(date.getTime()),holidayname);
                
                // Generate a UID for the event..
                Uid ug = new Uid();
                slgevnt.getProperties().add(ug);
                slgevnt.getProperties().add(new Description( holidaydes));
                
               /* ((Property) slgevnt.getProperties().getProperty(Property.DTSTART))
                .getParameters().add(Value.DATE);*/
                calendar.getComponents().add(slgevnt);
            }
            
            FileOutputStream fout = new FileOutputStream("tamilnaduHoliday"+year+".ics");

            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fout);

            
        }
        catch (Exception e) {
        	System.out.println(e);
        }
	}

	// http://www.tn.gov.in/holiday/2018
	// http://www.tamildailycalendar.com/tamil_festival_dates.php?Year=2019
	// https://www.drikpanchang.com/tamil/tamil-calendar.html
	
	
	
}
