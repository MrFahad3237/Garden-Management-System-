package View;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class StandingModel {

    private SearchOption option;
    private SearchOptionStatus statusOption;
    private Map<String, Long> standing;

}
