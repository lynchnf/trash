package norman.trash.controller.view;

import norman.trash.domain.Cat;

public class CatView {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CatView(Cat cat) {
        id = cat.getId();
        name = cat.getName();
    }
}
