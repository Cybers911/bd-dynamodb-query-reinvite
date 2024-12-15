package com.amazon.ata.dynamodbquery.dao;

import com.amazon.ata.dynamodbquery.dao.models.EventAnnouncement;
import com.amazon.ata.dynamodbquery.converter.ZonedDateTimeConverter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * Manages access to EventAnnouncement items.
 */
public class EventAnnouncementDao {

    private DynamoDBMapper mapper;
    
    // Define a constant to represent the Date/time converter we are using
    // This is done to make it easier if we need to change the converter
    // NOT REQUIRED FOR DYNAMODB ACCESS
    private static final ZonedDateTimeConverter ZONED_DATE_TIME_CONVERTER = new ZonedDateTimeConverter();

    /**
     * Creates an EventDao with the given DDB mapper.
     * @param mapper DynamoDBMapper
     */
    @Inject
    public EventAnnouncementDao(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Gets all event announcements for a specific event.
     *
     * @param eventId The event to get announcements for.
     * @return the list of event announcements.
     */
    public List<EventAnnouncement> getEventAnnouncements(String eventId) {
        // TODO: implement// it takes an event Id and should return a EventAnnouncement object

        EventAnnouncement eventAnnouncement = new EventAnnouncement();
        eventAnnouncement.setEventId(eventId); //we retrieve the eventId from the EventAnnouncement object
        //and passed it to the DynamoDBMapper's query method and get with the hashKey method'
        DynamoDBQueryExpression<EventAnnouncement> queryExpression = new DynamoDBQueryExpression<EventAnnouncement>()
                // We specify the partition key (eventId) and sort key (creationTime)

                        .withHashKeyValues(eventAnnouncement);// the value of the eventId corresponding to
        // the partition key
        //where match the eventId from the Event announcement

        //we call the DynamoDBMapper's query method to retrieve all event announcements for the given event
        // only the announcement belonging to this eventId'

        PaginatedQueryList<EventAnnouncement> queryList = mapper.query(EventAnnouncement.class, queryExpression);

        return queryList;

        /*// We need to convert the ZonedDateTime to a String using the converter
        // This is done to make it easier if we need to change the converter
        // NOT REQUIRED FOR DYNAMODB ACCESS
        eventAnnouncement.setAnnouncementTime(ZONED_DATE_TIME_CONVERTER.
        convertToAttributeValue(eventAnnouncement.getAnnouncementTime()));

        // Query DynamoDB to get all event announcements for the given event
        List<EventAnnouncement> announcements = mapper.query(EventAnnouncement.class, eventId,
                new DynamoDBMapper.QuerySpec().withHashKey("eventId", eventId));

        // Convert the ZonedDateTime back to a ZonedDateTime object
        announcements.forEach(a -> a.setAnnouncementTime(ZONED_DATE_TIME_CONVERTER.convertToObject
        (a.getAnnouncementTime())));

        return announcements;
        return Collections.emptyList();*/

    }

    /**
     * Get all event announcements posted between the given dates for the given event.
     *
     * @param eventId The event to get announcements for.
     * @param startTime The start time to get announcements for.
     * @param endTime The end time to get announcements for.
     * @return The list of event announcements.
     */
    public List<EventAnnouncement> getEventAnnouncementsBetweenDates(String eventId, ZonedDateTime startTime,
                                                                     ZonedDateTime endTime) {
        // TODO: implement

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":eventId", new AttributeValue().withS(eventId));
        valueMap.put(":startTme", new AttributeValue()
                .withS(ZONED_DATE_TIME_CONVERTER.convert(startTime)));
        valueMap.put(":endTme", new AttributeValue()
               .withS(ZONED_DATE_TIME_CONVERTER.convert(endTime)));

        DynamoDBQueryExpression<EventAnnouncement> queryExpression = new DynamoDBQueryExpression<EventAnnouncement>()
        .withKeyConditionExpression("eventId = :eventId and timePublished between :startTime and :endTime")
        .withExpressionAttributeValues(valueMap);
           return mapper.query(EventAnnouncement.class, queryExpression);
    }

    /**
     * Creates a new event announcement.
     *
     * @param eventAnnouncement The event announcement to create.
     * @return The newly created event announcement.
     */
    public EventAnnouncement createEventAnnouncement(EventAnnouncement eventAnnouncement) {
        mapper.save(eventAnnouncement);
        return eventAnnouncement;
    }
}
