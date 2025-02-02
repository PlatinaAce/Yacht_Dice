//게임 진행 규칙, 점수 계산, 주사위 굴리기, 턴 관리 등의 핵심 로직

import java.util.Arrays;

class Game {
    private Dice[] dice; // 주사위 객체 배열
    private int rollsLeft; // 남은 주사위 굴리기 횟수
    private int[] scoreCard; // 점수 카드 배열
    private int[] recordCard; // 저장된 점수 카드
    private boolean scoreSubmitted; // 점수 제출 여부 (한 턴에 한 번)

    public Game() {
        dice = new Dice[5];
        for (int i = 0; i < 5; i++) {
            dice[i] = new Dice();
        }
        rollsLeft = 3;
        scoreCard = new int[15];
        recordCard = new int[15];
        Arrays.fill(scoreCard, 0); // 모든 값 0으로 초기화
        Arrays.fill(recordCard, 0);
        scoreSubmitted = false; // 초기화 시 점수 제출 여부 false
    }


    // 주사위 배열을 반환하는 메서드
    public Dice[] getDice() {
        return dice;
    }

    // 점수 카드 배열을 반환하는 메서드
    public int[] getScoreCard() {
        return scoreCard;
    }

    // 기록 카드 배열 반환
    public int[] getRecordCard() {
        return recordCard;
    }

    // 남은 주사위 굴리기 횟수를 반환하는 메서드
    public int getRollsLeft() {
        return rollsLeft;
    }

    // 주사위 굴리기 메서드
    public void rollDice(boolean[] keepFlags) {
        // keepFlags 배열에 따라, 주사위를 유지할지 말지 결정
        for (int i = 0; i < 5; i++) {
            if (!keepFlags[i]) { // 해당 주사위가 선택되지 않으면 굴린다
                dice[i].roll(); // 주사위 굴리기
            }
        }
        rollsLeft--; // 주사위 굴린 횟수 차감
        scoreSubmitted = false; // 주사위를 굴렸으므로 점수 제출 가능
    }

    // 점수 계산 메서드
    public void calculateScores() {
        int[] diceValues = new int[6]; // 주사위 값에 대한 빈도 배열 (1부터 6까지)

        // 각 주사위의 값에 따라 빈도 계산
        for (Dice die : dice) {
            diceValues[die.getValue() - 1]++;
        }

        // Aces ~ Sixes 점수 계산
        for (int i = 0; i < 6; i++) {
            scoreCard[i] = diceValues[i] * (i + 1);
        }

        // Choice 점수 계산
        scoreCard[8] = Arrays.stream(dice).mapToInt(Dice::getValue).sum();

        // Four of a Kind 점수 계산
        boolean hasFour = Arrays.stream(diceValues).anyMatch(count -> count >= 4);
        scoreCard[9] = hasFour ? scoreCard[8] : 0;

        // Full House 점수 계산
        boolean hasThree = Arrays.stream(diceValues).anyMatch(count -> count == 3);
        boolean hasTwo = Arrays.stream(diceValues).anyMatch(count -> count == 2);
        scoreCard[10] = (hasThree && hasTwo) ? scoreCard[8] : 0;

        // Small Straight 점수 계산
        scoreCard[11] = containsSequence(diceValues, 4) ? 15 : 0;

        // Large Straight 점수 계산
        scoreCard[12] = containsSequence(diceValues, 5) ? 30 : 0;

        // Yacht 점수 계산
        boolean hasYacht = Arrays.stream(diceValues).anyMatch(count -> count == 5);
        scoreCard[13] = hasYacht ? 50 : 0;
    }

    public void calculateTotal(boolean[] scoresLocked) {
        // Subtotal (Aces ~ Sixes 합계)
        recordCard[6] = 0;
        for (int i = 0; i < 6; i++) {
            if (scoresLocked[i]) {
                recordCard[6] += recordCard[i]; // 잠긴 점수만 합산
            }
        }

        // Bonus (Subtotal이 63 이상이면 35점)
        recordCard[7] = (recordCard[6] >= 63) ? 35 : 0;

        // Total (전체 점수 합산)
        recordCard[14] = recordCard[6] + recordCard[7]; // Subtotal과 Bonus 추가
        for (int i = 8; i < 14; i++) {
            if (scoresLocked[i]) {
                recordCard[14] += recordCard[i]; // 잠긴 점수만 Total에 합산
            }
        }
    }

    private boolean containsSequence(int[] diceValues, int length) {
        int consecutive = 0; // 연속된 숫자의 길이를 추적하는 변수
        for (int count : diceValues) {
            if (count > 0) { // 숫자가 있으면 연속 길이 증가
                consecutive++;
                if (consecutive >= length) return true; // 연속 길이가 충분하면 true 반환
            } else {
                consecutive = 0; // 연속이 끊기면 초기화
            }
        }
        return false; // 조건에 맞는 연속이 없으면 false 반환
    }

    // 점수 제출 여부 확인
    public boolean isScoreSubmitted() {
        return scoreSubmitted;
    }

    // 점수 제출 여부 설정
    public void setScoreSubmitted(boolean submitted) {
        scoreSubmitted = submitted;
    }

    // 턴을 리셋하는 메서드
    public void resetTurn() {
        rollsLeft = 3; // 남은 주사위 굴리기 횟수를 3으로 초기화
    }
}


