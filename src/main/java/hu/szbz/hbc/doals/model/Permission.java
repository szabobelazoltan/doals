package hu.szbz.hbc.doals.model;

public enum Permission {
    READ(1, 'r'),
    WRITE(2, 'w'),
    DELETE(4, 'd')
    ;

    private final int code;
    private final char letter;

    Permission(int code, char letter) {
        this.code = code;
        this.letter = letter;
    }

    public boolean matches(int inputPermissionCode) {
        return (this.code & inputPermissionCode) == this.code;
    }

    public static String mapToCombinationString(int inputPermissionCode) {
        final StringBuilder permissionCombinationString = new StringBuilder();
        for (Permission permission : values()) {
            if (permission.matches(inputPermissionCode)) {
                permissionCombinationString.append(permission.letter);
            }
        }
        return permissionCombinationString.toString();
    }

    public static int mapToCode(Permission... permissions) {
        int codeCombination = 0;
        for (Permission permission : permissions) codeCombination |= permission.code;
        return codeCombination;
    }
}
