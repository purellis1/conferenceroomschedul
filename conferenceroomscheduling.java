import java.time.LocalTime;
import java.util.*;

public class ConferenceRoomScheduling {

    private static final String data = "7.11,8,09:00,09:15,14:30,15:00 8.23,6,09:00,09:15,10:00,11:00,14:00,15:00 8.43,7,11:30,12:30,17:00,17:30 9.511,9,09:30,10:30,12:00,12:15,15:15,16:15 9.527,4,09:00,11:00,14:00,16:00 9.547,8,10:30,11:30,13:30,15:30,16:30,17:30";
    // Map of available times to room
    private static final Map<Float, Integer> seating_count_map;
    // Map of room to meeting times available
    private static final Map<List<String>, List<Float>> available_meeting_times_map;


    static {
        // Load the sample rooms data in to maps
        seating_count_map = new HashMap<>();
        available_meeting_times_map = new HashMap<>();
        String[] rooms_info = data.split(" ");
        for(String room_info: rooms_info) {
            String[] parsed_info = room_info.split(",", 3);
            int seating_count = Integer.parseInt(parsed_info[1]);
            Float floor_room_number = Float.parseFloat(parsed_info[0]);

            // load into seating_count_map
            seating_count_map.put(floor_room_number, seating_count);

            // load into available_meeting_times_map
            List<String> available_meeting_times = Arrays.asList(parsed_info[2].split(","));
            int count = 0;
            while(count < available_meeting_times.size()) {
                List<String> times = new ArrayList<>();
                times.add(available_meeting_times.get(count));
                times.add(available_meeting_times.get(count+1));
                if (available_meeting_times_map.containsKey(times)) {
                    List<Float> floor_numbers = available_meeting_times_map.get(times);
                    floor_numbers.add(floor_room_number);
                    available_meeting_times_map.put(times, floor_numbers);
                } else {
                    List<Float> floor_numbers = new ArrayList<>();
                    floor_numbers.add(floor_room_number);
                    available_meeting_times_map.put(times, floor_numbers);
                }
                count = count + 2;
            }
        }
    }

    private Float scheduleRoom(String input) {

        // parse the input
        String[] parsed_input = input.split(",");
        int seating_count = Integer.parseInt(parsed_input[0]);
        int floor_number = Integer.parseInt(parsed_input[1]);
        String starting_time = parsed_input[2];
        String ending_time = parsed_input[3];

        List<Float> available_rooms_for_times = new ArrayList<>();

        /**
         * check if any rooms are available for time within the times given - for say if a room is available from
         * 13:30-15:30, this allows us to consider this room for meeting to be scheduled between 14:00-15:00
         */

        Set<List<String>> keyset = available_meeting_times_map.keySet();
        for (List<String> key : keyset) {
            if ((LocalTime.parse(starting_time).isAfter(LocalTime.parse(key.get(0))) ||
                    LocalTime.parse(starting_time).equals(LocalTime.parse(key.get(0)))) &&
                            LocalTime.parse(ending_time).isBefore(LocalTime.parse(key.get(1))) ||
                    (LocalTime.parse(ending_time).equals(LocalTime.parse(key.get(1))))) {
                available_rooms_for_times.addAll(available_meeting_times_map.get(key));
            }
        }

        // check if the available rooms fits the team count
        List<Float> available_rooms = new ArrayList<>();
        for (Float room: available_rooms_for_times) {
            if (seating_count_map.get(room) >= seating_count) {
                available_rooms.add(room);
            }
        }

        // if more than one room is available, find the closest room
        if (available_rooms.size() > 1) {
            float distance = Math.abs(available_rooms.get(0) - floor_number);
            int least_distance_index = 0;

            for(int count = 1; count < available_rooms.size(); count++){
                float current_distance = Math.abs(available_rooms.get(count) - floor_number);
                if(current_distance < distance){
                    least_distance_index = count;
                    distance = current_distance;
                }
            }
            return available_rooms.get(least_distance_index);
        } else {
            return available_rooms.get(0);
        }
    }

    public static void main(String[] args) {
        ConferenceRoomScheduling conferenceRoomScheduling = new ConferenceRoomScheduling();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the input:");
        String input = scanner.nextLine();
        System.out.println("Closest Room available: " + conferenceRoomScheduling.scheduleRoom(input));
    }
}
