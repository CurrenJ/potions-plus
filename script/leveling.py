if __name__ == "__main__":
    a = 2
    b = 1.2
    c = 4
    d = 15
    levelToPoints = lambda x : (x ** a + c + (d * x)) ** b
    levelToPointsMap = {level: round(levelToPoints(level), 0) for level in range(1, 101)}
    diff = {level: round(points - levelToPoints(level - 1), 0) for level, points in levelToPointsMap.items() if level > 1}

    # truncate the decimal points to 2
    for level, points in levelToPointsMap.items():
        print(f'Level {level}: {points}' + (f' (+{diff[level]})' if level > 1 else ''))