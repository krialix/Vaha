module.exports = () => {
    const faker = require("faker");
    const _ = require("lodash");

    const generateRating = () => {
        return 5 / faker.random.number({ min: 1, max: 4 });
    }

    const pendingUserRequestList = () => {
        return faker.random.boolean()
            ? _.times(3, (n) => {
                return {
                    id: faker.random.uuid(),
                    displayName: faker.name.findName(),
                    rating: generateRating(),
                }
            })
            : []
    }

    const generateQuestion = () => {
        const isOwner = faker.random.boolean();

        const answerer = isOwner ? null : faker.random.boolean() ? {
            id: faker.random.uuid(),
            displayName: faker.name.findName()
        } : null;

        const requestSent = answerer ? false : faker.random.boolean();

        const requestEnabled = requestSent;

        return {
            id: faker.random.uuid(),
            user: {
                id: faker.random.uuid(),
                displayName: faker.name.findName(),
                isOwner: isOwner
            },
            category: {
                id: faker.random.number(10),
                displayName: faker.name.findName(),
                image: "https://placeimg.com/200/200/nature"
            },
            content: faker.lorem.text(3),
            answerer: answerer,
            isRequestSent: requestSent,
            isRequestEnabled: requestEnabled,
            createdAt: faker.date.past(10),
            pendingUserRequests: pendingUserRequestList()
        }
    }

    return {
        categories: _.times(10, (n) => {
            return {
                id: n + 1,
                displayName: faker.name.findName(),
                image: "https://placeimg.com/200/200/nature"
            }
        }),
        questions: {
            items: _.times(100, (n) => {
                return generateQuestion()
            }),
            nextPageToken: faker.random.uuid()
        },
        me: {
            id: faker.random.uuid(),
            username: faker.name.findName(),
            answerCount: faker.random.number(50),
            questionCount: faker.random.number(50),
            availableQuestionCount: faker.random.number(5),
            rating: generateRating(),
        }
    };
}